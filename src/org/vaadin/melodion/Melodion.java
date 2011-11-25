/**
 * This file Copyright (c) 2011 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.vaadin.melodion;

import java.util.Collection;
import java.util.HashSet;

import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.client.ui.VAnimatorProxy.AnimType;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

/**
 * A light-weight accordion, loosely based on
 * com.vaadin.addon.chameleon.SidebarMenu and
 * org.vaadin.jouni.animator.Disclosure.
 * 
 * @author Marlon Richert @ Vaadin
 */
public class Melodion extends CssLayout {

    private final Collection<NativeButton> buttons = new HashSet<NativeButton>();

    private final Collection<Tab> tabs = new HashSet<Tab>();

    /**
     * Creates a new melodion.
     */
    public Melodion() {
        setStyleName("melodion");
        addStyleName("sidebar-menu");
        setSizeUndefined();
        setWidth(20, UNITS_EM);
    }

    /**
     * Selects the given component in this melodion.
     * 
     * @param c
     *            The component to select.
     */
    public void setSelected(Component c) {
        updateStyles();
        if (c instanceof Tab) {
            Tab t = (Tab) c;
            t.label.addStyleName("selected");
            t.expand();
        } else {
            c.addStyleName("selected");
        }
    }

    private void updateStyles() {
        for (NativeButton b : buttons) {
            b.removeStyleName("selected");
        }
        for (Tab t : tabs) {
            t.label.removeStyleName("selected");
        }
    }

    /**
     * Collapse all tabs in this melodion, except for the given tab
     * 
     * @param tab
     *            The tab not to collapse.
     */
    public void collapseOthers(Tab tab) {
        for (Tab t : tabs) {
            if (t != tab) {
                t.collapse();
            }
        }
    }

    /**
     * Adds a new tab with the given label.
     * 
     * @param label
     *            The new tab's label.
     * @return The new tab.
     */
    public Tab addTab(Label label) {
        Tab tab = new Tab(label);
        tabs.add(tab);
        addComponent(tab);
        return tab;
    }

    /**
     * Adds a spacer to this melodion.
     * 
     * @return The new spacer.
     */
    public Component addSpacer() {
        Component spacer = new Label();
        spacer.setStyleName("spacer");
        addComponent(spacer);
        return spacer;
    }

    /**
     * Top-level menu item in a Melodion.
     * 
     * @author Marlon Richert @ Vaadin
     */
    public class Tab extends CssLayout {

        private final AnimatorProxy animator = new AnimatorProxy();

        private final Label label;

        private CssLayout content = null;

        private boolean expanded = false;

        private Tab(Label label) {
            this.label = label;
            this.label.setSizeUndefined();
            addComponent(animator);
            addComponent(this.label);

            addListener(new LayoutClickListener() {

                public void layoutClick(LayoutClickEvent event) {
                    if (event.getChildComponent() == Tab.this.label) {
                        setSelected(Tab.this);
                    }
                }
            });
        }

        /**
         * Adds a new button to this tab.
         * 
         * @param b
         *            The button to use.
         */
        public void addButton(final NativeButton b) {
            if (content == null) {
                content = new CssLayout();
            }
            buttons.add(b);
            content.addComponent(b);

            b.addListener(new Button.ClickListener() {

                public void buttonClick(ClickEvent event) {
                    setSelected(b);
                }
            });
        }

        /**
         * Expands this tab, revealing its contents.
         */
        public void expand() {
            if (!isExpanded()) {
                if (content != null) {
                    if (content.getParent() != this) {
                        addComponent(content);
                    }
                    animator.animate(content, AnimType.ROLL_DOWN_OPEN_POP);
                    expanded = true;
                }
                collapseOthers(this);
            }
        }

        /**
         * Whether this tab is showing its contents.
         * 
         * @return <code>true</code> if this tab is showing its contents;
         *         otherwise, <code>false</code>.
         */
        public boolean isExpanded() {
            return expanded;
        }

        /**
         * Collapses this tab, hiding its contents.
         */
        public void collapse() {
            if (content != null && isExpanded()) {
                animator.animate(content, AnimType.ROLL_UP_CLOSE_REMOVE);
                expanded = false;
            }
        }
    }
}
