/*
 * GUICraft - The Ultimate GUI System
 * Copyright (C) 2018 bundabrg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.com.grieve.bcf;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArgumentParser {
    private final CommandManager manager;
    @Getter
    private TreeNode<ArgData> data = new TreeNode<>();

    public ArgumentParser(CommandManager manager) {
        this.manager = manager;
    }

    public ArgumentParser(CommandManager manager, String path) {
        this.manager = manager;
        if (path != null) {
            createNode(path);
        }
    }


    public String walkTree() {
        StringBuilder result = new StringBuilder();

        for (TreeNode<ArgData> n : data.children) {
            result.append(walkTree(n, 0));
        }
        return result.toString();
    }

    private String walkTree(TreeNode<ArgData> node, int depth) {
        StringBuilder result = new StringBuilder();

        char[] repeat = new char[depth];
        Arrays.fill(repeat, ' ');
        String pad = new String(repeat);

        // Our name
        result.append(pad).append(node.data.arg).append("\n");

        for (TreeNode<ArgData> n : node.children) {
            result.append(walkTree(n, depth + 1));
        }
        return result.toString();
    }

    /**
     * Create node(s) designated by path including any missing parent nodes
     */
    public List<TreeNode<ArgData>> createNode(String path) {
        List<TreeNode<ArgData>> currentNodes = Collections.singletonList(data);

        StringReader reader = new StringReader(path);
        for (List<ArgData> argDataList : ArgData.parse(reader)) {
            List<TreeNode<ArgData>> newCurrent = new ArrayList<>();
            for (TreeNode<ArgData> node : currentNodes) {
                for (ArgData argData : argDataList) {
                    newCurrent.add(node.children.stream()
                            .filter(c -> c.data.arg.equals(argData.arg))
                            .findFirst()
                            .orElseGet(() -> node.addChild(argData)));
                }
            }
            currentNodes = newCurrent;
        }

        if (currentNodes.size() > 0 && currentNodes.get(0) == data) {
            return new ArrayList<>();
        }

        return currentNodes;
    }

    public List<Object> getData(CommandSender sender, String[] args) {
        return getData(sender, Arrays.asList(args), data);
    }

    private List<Object> getData(CommandSender, List<String> args, TreeNode<ArgData> node) {

    }


    public List<String> getAlternatives(CommandSender sender, String[] args) {
        return getAlternatives(sender, Arrays.asList(args), data);
    }

    private List<String> getAlternatives(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
        List<String> result = new ArrayList<>();

        if (args.size() == 0) {
            return result;
        }

        if (!node.isRoot() && node.data != null && node.data.arg != null) {
            Parser parser = manager.getParser(node.data.arg);

            if (parser == null) {
                return result;
            }

            ValidArgument valid = parser.isValid(sender, args, node);

            if (valid.isInvalid()) {
                return result;
            }

            if (valid.isPartial()) {
                result.addAll(valid.getPartials());
            }
        }

        // Recurse
        for (TreeNode<ArgData> n : node.children) {
            result.addAll(getAlternatives(sender, new ArrayList<>(args), n));
        }

        return result;
    }

}
