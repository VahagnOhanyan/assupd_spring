/*
 * Copyright (c) 2008-2022
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package com.ctp.asupdspring.controllers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

/**
 * todo Document type FxmlViewAccessController
 */
@Component
public class FxmlViewAccessController {

    public static void resolveAccess(Node node) {
        if (node instanceof MenuBar) {
            if (node.getId() != null && ContextController.authorities.containsKey(node.getId())) {
                int access_type = ContextController.authorities.get(node.getId());
                if (access_type == 4) {
                    node.setVisible(false);
                }
            } else {
                MenuBar menuBar = (MenuBar) node;
                for (Menu menu : menuBar.getMenus()) {
                    System.out.println("menu: " + menu);
                    for (MenuItem menuItem : menu.getItems()) {
                        if (menuItem.getId() != null && ContextController.authorities.containsKey(menuItem.getId())) {
                            int access_type = ContextController.authorities.get(menuItem.getId());
                            if (access_type == 4) {
                                menuItem.setVisible(false);
                            }
                        }
                    }
                }
            }
        }
        if (node instanceof ToolBar) {
            if (node.getId() != null && ContextController.authorities.containsKey(node.getId())) {
                int access_type = ContextController.authorities.get(node.getId());
                if (access_type == 4) {
                    node.setVisible(false);
                }
            } else {
                ToolBar toolBar = (ToolBar) node;
                for (Node n : toolBar.getItems()) {
                    if (n instanceof Parent) {
                        if (n.getId() != null && ContextController.authorities.containsKey(n.getId())) {
                            int access_type = ContextController.authorities.get(n.getId());
                            if (access_type == 4) {
                                n.setVisible(false);
                            }
                        } else {
                            Parent parent = (Parent) n;
                            resolveAccess(parent);
                        }
                    }
                }
            }
        }
        if (node instanceof MenuButton) {
            if (node.getId() != null && ContextController.authorities.containsKey(node.getId())) {
                int access_type = ContextController.authorities.get(node.getId());
                if (access_type == 4) {
                    node.setVisible(false);
                }
            } else {
                MenuButton menuButton = (MenuButton) node;
                for (MenuItem menuItem : menuButton.getItems()) {
                    if (menuItem.getId() != null && ContextController.authorities.containsKey(menuItem.getId())) {
                        int access_type = ContextController.authorities.get(menuItem.getId());
                        if (access_type == 4) {
                            menuItem.setVisible(false);
                        }
                    }
                }
            }
        }
        if (node instanceof SplitPane) {
            if (node.getId() != null && ContextController.authorities.containsKey(node.getId())) {
                int access_type = ContextController.authorities.get(node.getId());
                if (access_type == 4) {
                    node.setVisible(false);
                }
            } else {
                SplitPane splitPane = (SplitPane) node;
                for (Node n : splitPane.getItems()) {
                    if (n instanceof Parent) {
                        if (n.getId() != null && ContextController.authorities.containsKey(n.getId())) {
                            int access_type = ContextController.authorities.get(n.getId());
                            if (access_type == 4) {
                                n.setVisible(false);
                            }
                        } else {
                            Parent parent = (Parent) n;
                            resolveAccess(parent);
                        }
                    }
                }
            }
        }

        if (node instanceof Parent) {
            if (node.getId() != null && ContextController.authorities.containsKey(node.getId())) {
                int access_type = ContextController.authorities.get(node.getId());
                if (access_type == 4) {
                    node.setVisible(false);
                }
            } else {
                Parent parent = (Parent) node;
                for (Node n : parent.getChildrenUnmodifiable()) {
                    if (n instanceof Parent) {
                        if (n.getId() != null && ContextController.authorities.containsKey(n.getId())) {
                            int access_type = ContextController.authorities.get(n.getId());
                            if (access_type == 4) {
                                n.setVisible(false);
                            }
                        } else {
                            Parent parent1 = (Parent) n;
                            resolveAccess(parent1);
                        }
                    }
                }
            }
        }
    }
}
