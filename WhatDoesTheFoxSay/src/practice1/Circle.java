/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practice1;

import java.awt.Color;

/**
 *
 * @author Alejandro Sánchez Aristizábal y Santiago Vanegas Gil.
 */
public class Circle {
    
    public double xCenter, yCenter, radius;
    public Color color;
    public Vector2D finalPoint;


    public Circle(double xCenter, double yCenter, double radius, Color color) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.radius = radius;
        this.color = color;
        //sFactor es 0 pero eso no afecta el propósito.
        this.finalPoint = new Vector2D(0.0, 0.0, 0.0);
        finalPoint.setCX(xCenter);
        finalPoint.setCY(yCenter + radius);
        //radius es 0 pero eso no afecta el propósito.
        finalPoint.setR(1.0);
    }
    
    public void setFinalPoint(double xCenter, double yCenter){
        this.finalPoint.setCX(xCenter);
        this.finalPoint.setCY(yCenter + radius);
    }
}
