/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2013, 2014, 2016, 2022  AO Industries, Inc.
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
 * along with semanticcms-theme-documentation.  If not, see <https://www.gnu.org/licenses/>.
 */
semanticcms_theme_documentation_navigation = {
  /**
   * The TreeView object.
   * This is set in navigation.jspx
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
    if (highlightNode === traverseNode) traverseNode.highlight();
    else traverseNode.unhighlight();
    for (index in traverseNode.children) {
      semanticcms_theme_documentation_navigation._highlightNode(highlightNode, traverseNode.children[index]);
    }
  },

  /**
   * Highlights the given node.
   * Unhighlights all other nodes.
   */
  highlightNode : function(node) {
    semanticcms_theme_documentation_navigation._highlightNode(node, semanticcms_theme_documentation_navigation.tree.getRoot());
  },

  /**
   * Gets the page path class for a given node.
   */
  getEncodedPagePath : function(node) {
    var data = node.data;
    if (data !== undefined && data !== null) {
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
    semanticcms_theme_documentation_navigation._getNodeIndexCounter++;
    if (node === traverseNode) {
      semanticcms_theme_documentation_navigation._getNodeIndexPos = semanticcms_theme_documentation_navigation._getNodeIndexCounter;
    } else {
      for (index in traverseNode.children) {
        semanticcms_theme_documentation_navigation._getNodeIndex(node, traverseNode.children[index]);
        if (semanticcms_theme_documentation_navigation._getNodeIndexPos !== -1) {
          break;
        }
      }
    }
  },

  /**
   * Gets the index of a node or <code>-1</code> if not found.
   */
  getNodeIndex : function(node) {
    semanticcms_theme_documentation_navigation._getNodeIndexCounter = -1;
    semanticcms_theme_documentation_navigation._getNodeIndexPos = -1;
    if (node !== undefined && node !== null) {
      semanticcms_theme_documentation_navigation._getNodeIndex(node, semanticcms_theme_documentation_navigation.tree.getRoot());
    }
    return semanticcms_theme_documentation_navigation._getNodeIndexPos;
  },

  /**
   * Expands a node and all of its parents.
   */
  expandNode : function(node) {
    node.expand();
    var parent = node.parent;
    if (parent !== semanticcms_theme_documentation_navigation.tree.getRoot()) semanticcms_theme_documentation_navigation.expandNode(parent);
  },

  /**
   * Recursive component of highlightPage.
   */
  _highlightPage : function(encodedPagePath, traverseNode, nodesHighlighted) {
    if (encodedPagePath === semanticcms_theme_documentation_navigation.getEncodedPagePath(traverseNode)) {
      traverseNode.highlight();
      semanticcms_theme_documentation_navigation.expandNode(traverseNode);
      nodesHighlighted.push(traverseNode);
    } else {
      traverseNode.unhighlight();
    }
    for (index in traverseNode.children) {
      semanticcms_theme_documentation_navigation._highlightPage(encodedPagePath, traverseNode.children[index], nodesHighlighted);
    }
  },

  /**
   * Highlights all nodes that links to the given page.
   * Unhighlights nodes that do not match.
   */
  highlightPage : function(encodedPagePath) {
    if (
      semanticcms_theme_documentation_navigation.nodeLastSelected === null
      || semanticcms_theme_documentation_navigation.getEncodedPagePath(semanticcms_theme_documentation_navigation.nodeLastSelected) !== encodedPagePath
    ) {
      var lastSelectedIndex = semanticcms_theme_documentation_navigation.getNodeIndex(semanticcms_theme_documentation_navigation.nodeLastSelected);
      semanticcms_theme_documentation_navigation.tree.collapseAll();
      var nodesHighlighted = new Array();
      semanticcms_theme_documentation_navigation._highlightPage(encodedPagePath, semanticcms_theme_documentation_navigation.tree.getRoot(), nodesHighlighted);
      // Focus to the node closest to the last one selected
      var closestNode = null;
      var closestDistance = null;
      for (index in nodesHighlighted) {
        var node = nodesHighlighted[index];
        var nodeDistance = Math.abs(lastSelectedIndex - semanticcms_theme_documentation_navigation.getNodeIndex(node));
        if (closestNode === null || nodeDistance < closestDistance) {
          closestNode = node;
          closestDistance = nodeDistance;
        }
      }
      if (closestNode !== null) {
        closestNode.focus();
        semanticcms_theme_documentation_navigation.nodeLastSelected = closestNode;
      }
    }
  },

  /**
   * Called when a node is clicked.
   */
  treeClickEvent: function(args) {
    semanticcms_theme_documentation_navigation.nodeLastSelected = args.node;
    semanticcms_theme_documentation_navigation.highlightNode(args.node);
  },

  /**
   * Called when enter is pressed.
   */
  treeEnterKeyPressed: function(node) {
    semanticcms_theme_documentation_navigation.nodeLastSelected = node;
    semanticcms_theme_documentation_navigation.highlightNode(node);
  }
};
