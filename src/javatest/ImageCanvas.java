/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javatest;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/**
 *
 * @author Romain Fenouil
 */
public class ImageCanvas extends JPanel
{
    private Image m_image;
    
    
    public ImageCanvas()
    {
        this(new String(""));
    }
    
    public ImageCanvas(String name) 
    {   
        MediaTracker media = new MediaTracker(this);
        m_image = Toolkit.getDefaultToolkit().getImage(name);
        
        media.addImage(m_image, 0);
        try 
        {
            media.waitForID(0);
        }
        catch (Exception e) {}
        //addMouseListener(this);
        //this.setPreferredSize(new Dimension(getWidth(),100));
    }

    public void draw(Graphics g, int width, int height)
    {
        g.drawImage(m_image, 0, 0, width, height, this);
        //g.drawLine(100, 100, 400, 400);
    }
    
    public void paintComponent(Graphics g) 
    {
        draw(g, getWidth(), getHeight());
    }
    
    public void save(Graphics g) 
    {
        draw(g, 1024, 1024);
    }
        
}