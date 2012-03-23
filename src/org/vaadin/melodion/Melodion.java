/*
 * Copyright 2011 Vaadin Ltd.
 *
 * Licensed under the GNU Affero General Public License, Version 3.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.melodion;

import java.util.*;

import org.vaadin.jouni.animator.*;
import org.vaadin.jouni.animator.client.ui.VAnimatorProxy.AnimType;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

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
