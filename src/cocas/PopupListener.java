/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cocas;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPopupMenu;

/**
 *
 * @author cauchy
 */
class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }

