/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2013, 2014, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-theme-documentation.
 *
 * semanticcms-theme-documentation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-theme-documentation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-theme-documentation.  If not, see <http://www.gnu.org/licenses/>.
 */
navigation = {
	/**
	 * The TreeView object.
	 * This is set in navigation.jsp
	 */
	tree : null,

	/**
	 * The node last selected.
	 */
	nodeLastSelected : null,

	/**
	 * Recursive component of highlightNode.
	 */
	_highlightNode : function(highlightNode, traverseNode) {
		if(highlightNode === traverseNode) traverseNode.highlight();
		else traverseNode.unhighlight();
		for(index in traverseNode.children) {
			navigation._highlightNode(highlightNode, traverseNode.children[index]);
		}
	},

	/**
	 * Highlights the given node.
	 * Unhighlights all other nodes.
	 */
	highlightNode : function(node) {
		navigation._highlightNode(node, navigation.tree.getRoot());
	},

	/**
	 * Gets the page path class for a given node.
	 */
	getEncodedPagePath : function(node) {
		var data = node.data;
		if(data !== undefined && data !== null) {
			return data;
		}
		return null;
	},

	/**
	 * Used by getNodeIndex.
	 */
	_getNodeIndexCounter : -1,
	_getNodeIndexPos : -1,

	/**
	 * Recursive component of highlightNode.
	 */
	_getNodeIndex : function(node, traverseNode) {
		navigation._getNodeIndexCounter++;
		if(node === traverseNode) {
			navigation._getNodeIndexPos = navigation._getNodeIndexCounter;
		} else {
			for(index in traverseNode.children) {
				navigation._getNodeIndex(node, traverseNode.children[index]);
				if(navigation._getNodeIndexPos !== -1) break;
			}
		}
	},

	/**
	 * Gets the index of a node or <code>-1</code> if not found.
	 */
	getNodeIndex : function(node) {
		navigation._getNodeIndexCounter = -1;
		navigation._getNodeIndexPos = -1;
		if(node !== undefined && node !== null) {
			navigation._getNodeIndex(node, navigation.tree.getRoot());
		}
		return navigation._getNodeIndexPos;
	},

	/**
	 * Expands a node and all of its parents.
	 */
	expandNode : function(node) {
		node.expand();
		var parent = node.parent;
		if(parent !== navigation.tree.getRoot()) navigation.expandNode(parent);
	},

	/**
	 * Recursive component of highlightPage.
	 */
	_highlightPage : function(encodedPagePath, traverseNode, nodesHighlighted) {
		if(encodedPagePath === navigation.getEncodedPagePath(traverseNode)) {
			traverseNode.highlight();
			navigation.expandNode(traverseNode);
			nodesHighlighted.push(traverseNode);
		} else {
			traverseNode.unhighlight();
		}
		for(index in traverseNode.children) {
			navigation._highlightPage(encodedPagePath, traverseNode.children[index], nodesHighlighted);
		}
	},

	/**
	 * Highlights all nodes that links to the given page.
	 * Unhighlights nodes that do not match.
	 */
	highlightPage : function(encodedPagePath) {
		if(
			navigation.nodeLastSelected === null
			|| navigation.getEncodedPagePath(navigation.nodeLastSelected) !== encodedPagePath
		) {
			var lastSelectedIndex = navigation.getNodeIndex(navigation.nodeLastSelected);
			navigation.tree.collapseAll();
			var nodesHighlighted = new Array();
			navigation._highlightPage(encodedPagePath, navigation.tree.getRoot(), nodesHighlighted);
			// Focus to the node closest to the last one selected
			var closestNode = null;
			var closestDistance = null;
			for(index in nodesHighlighted) {
				var node = nodesHighlighted[index];
				var nodeDistance = Math.abs(lastSelectedIndex - navigation.getNodeIndex(node));
				if(closestNode === null || nodeDistance < closestDistance) {
					closestNode = node;
					closestDistance = nodeDistance;
				}
			}
			if(closestNode !== null) {
				closestNode.focus();
				navigation.nodeLastSelected = closestNode;
			}
		}
	},

	/**
	 * Called when a node is clicked.
	 */
	treeClickEvent: function(args) {
		navigation.nodeLastSelected = args.node;
		navigation.highlightNode(args.node);
	},

	/**
	 * Called when enter is pressed.
	 */
	treeEnterKeyPressed: function(node) {
		navigation.nodeLastSelected = node;
		navigation.highlightNode(node);
	}
};
