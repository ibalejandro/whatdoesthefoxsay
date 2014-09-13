/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package practice1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Transformaciones en 2D
 * 
 * Basado en explicación sobre uso de Hilos: 
 * http://stackoverflow.com/questions/3489543/how-to-call-a-method-with-a-separate-thread-in-java
 * 
 * Basado en explicación sobre Java MouseListener: 
 * http://docs.oracle.com/javase/tutorial/uiswing/events/mousemotionlistener.html
 * @author Alejandro Sánchez Aristizábal y Santiago Vanegas Gil.
 * 
 * 
 */
public class Practice1 extends JPanel implements MouseListener {

    private Graphics2D g2d;
    
    private int w;
    private int h;
    private int difficulty = 1; 
    private int clickCounter = 0;
    private int correctRounds = 0;
    private boolean areCirclesAlreadyGenerated = false;
    private boolean isSequenceAlreadyGenerated = false;
    private boolean areCirclesExpanded = true;
    private boolean isThePanelScaling = false;
    private boolean isTheCircleRotating = false;
    private boolean isPlayerTurn = false;
    private boolean wereInstructionsAlreadyShown = false;
    private ArrayList<Circle> circles = new ArrayList<>();
    private ArrayList<Circle> foxSequence = new ArrayList<>();
    private ArrayList<Circle> userSequence = new ArrayList<>();
    private Circle circleToScale; //Círculo para pintar más grande que los
                                  //demás.
    private Circle circleToRotate; //Círculo para pintarle su línea rotada
                                   //cuando el usuario haga click sobre él.
    private final int FOX_TIME = 500;
    private final int USER_TIME = 1000;
    private final double TRANSLATE_FACTOR = 75.0;
    private final double SCALE_FACTOR = 2.0; //Los círculos doblan su radio y 
                                             //luego vuelven a la posición 
                                             //original.
    private final double ROTATE_ANGLE = 45.0;
    JLabel correctSeqNumber;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g2d = (Graphics2D) g;

        // size es el tamaño de la ventana.
        Dimension size = getSize();
        // Insets son los bordes y los títulos de la ventana.
        Insets insets = getInsets();

        w =  size.width - insets.left - insets.right;
        h =  size.height - insets.top - insets.bottom;
        
        //Se hace para evitar llenar la lista con los mismos 5 círculos.
        if(!areCirclesAlreadyGenerated){
            //Se crean los círculos - botones del juego.
            areCirclesAlreadyGenerated = true;
            circles.add(new Circle(0.0, 0.0, 40.0, Color.RED));
            circles.add(new Circle(0.0, 150.0, 40.0, Color.YELLOW)); //y = 150
            circles.add(new Circle(150.0, 0.0, 40.0, Color.GREEN)); //x = 150
            circles.add(new Circle(0.0, -150.0, 40.0, Color.BLUE)); //y = -150
            circles.add(new Circle(-150.0, 0.0, 40.0, Color.MAGENTA)); //x = -150
            this.addMouseListener(this);
        }
        
        for(int i = 0; i < 5; i++){
            Circle c = circles.get(i);
            //En caso de entrar al primer if sabemos que se está en la secuencia
            //de escalamiento.
            if(isThePanelScaling && circleToScale.color == c.color){
                drawIniView(circleToScale);
            }else if(isTheCircleRotating && circleToRotate.color == c.color ){
                drawIniView(circleToRotate);
            }else{
                drawIniView(c);
            }
        }

        drawBaseCircle();
        
        startRound();
        
    }
    
    public void startRound(){
        if(!isSequenceAlreadyGenerated){
            isSequenceAlreadyGenerated = true;
            correctSeqNumber.setText(String.valueOf(correctRounds));
            generateRandomSequence();
            //Permite poder hacer el escalamiento sin bloquear el Main Thread.
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run(){
                    if(!wereInstructionsAlreadyShown){
                        wereInstructionsAlreadyShown = true;
                        String instructions = "Trate de repetir la mayor "
                                           + "cantidad de secuencias posibles.";
                        String instructionsTitle = "Instrucciones";
                        JOptionPane.showMessageDialog (null, instructions,
                                                       instructionsTitle, 
                                               JOptionPane.INFORMATION_MESSAGE);
                    }
                    try {
                        isPlayerTurn = false;
                        Thread.currentThread().sleep(USER_TIME + 500);
                        areCirclesExpanded = true;
                        translateToGamePosition();
                        repaint();
                        Thread.currentThread().sleep(USER_TIME);
                        areCirclesExpanded = false;
                        translateToGamePosition();
                        repaint();
                        Thread.currentThread().sleep(USER_TIME);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Practice1.class.getName()).
                                                    log(Level.SEVERE, null, ex);
                    }
                    
                    showSequence();
                }
            });  
            t1.start();
        }
    }
    
    public void drawIniView(Circle circle){
        double cX = circle.xCenter;
        double cY = circle.yCenter;
        double radius = circle.radius;
        Color c = circle.color;
        double finalPointX = circle.finalPoint.getCX();
        double finalPointY = circle.finalPoint.getCY();
        g2d.setColor(c);
        g2d.fillOval(mapX(cX)- (int) radius, mapY(cY)- (int) radius, 
                     (int) (radius*2), (int) (radius*2));
        g2d.setColor(Color.BLACK);
        //Dibuja la raya que va a indicar rotación.
        g2d.drawLine(mapX(cX), mapY(cY), mapX(finalPointX), mapY(finalPointY));
        g2d.drawLine(mapX(cX)+1, mapY(cY), mapX(finalPointX)+1, 
                                                             mapY(finalPointY));
        double x, y, d;
        
        x = 0.0;
        y = radius;
        
        d = 3 - (2 * y);
        
        while (x <= y) {
            drawPoints(cX, cY, x, y);
            if (d < 0) {
                d = d + (4 * x) + 6;
            } else {
                d = d + (4 * (x - y)) + 10;
                y--;
            }
            x++;
        }
        
    }
    
    public void drawPoints(double cX, double cY, double x, double y) {
         g2d.drawLine(mapX(cX+y), mapY(cY-x), mapX(cX+y), mapY(cY-x));  //1er oct
         g2d.drawLine(mapX(cX+x), mapY(cY-y), mapX(cX+x), mapY(cY-y));  //2do oct
         g2d.drawLine(mapX(cX-x), mapY(cY-y), mapX(cX-x), mapY(cY-y));  //3er oct
         g2d.drawLine(mapX(cX-y), mapY(cY-x), mapX(cX-y), mapY(cY-x));  //4to oct
         g2d.drawLine(mapX(cX-y), mapY(cY+x), mapX(cX-y), mapY(cY+x));  //5to oct
         g2d.drawLine(mapX(cX-x), mapY(cY+y), mapX(cX-x), mapY(cY+y));  //6to oct
         g2d.drawLine(mapX(cX+x), mapY(cY+y), mapX(cX+x), mapY(cY+y));  //7to oct
         g2d.drawLine(mapX(cX+y), mapY(cY+x), mapX(cX+y), mapY(cY+x));  //8avo oct
    }
    
    public void drawBaseCircle(){
        //Se pinta el borde
        drawExtCircle(0, 0, 250, Color.BLACK );
        drawExtCircle(0, 0, 251, Color.BLACK);
        drawExtCircle(0, 0, 252, Color.RED);
        drawExtCircle(0, 0, 253, Color.RED);
        drawExtCircle(0, 0, 254, Color.YELLOW);
        drawExtCircle(0, 0, 255, Color.YELLOW);
        drawExtCircle(0, 0, 254, Color.GREEN);
        drawExtCircle(0, 0, 255, Color.GREEN);
        drawExtCircle(0, 0, 256, Color.BLUE);
        drawExtCircle(0, 0, 257, Color.BLUE);
        drawExtCircle(0, 0, 258, Color.MAGENTA);
        drawExtCircle(0, 0, 259, Color.MAGENTA);
        drawExtCircle(0, 0, 260, Color.BLACK);
        drawExtCircle(0, 0, 261, Color.BLACK);
    }
    
    public void drawExtCircle(int cX, int cY, int radius, Color c){
        g2d.setColor(c);
        int x, y, d;
        
        x = 0;
        y = radius;
        
        d = 3 - (2 * y);
        
        while (x <= y) {
            drawPoints(cX, cY, x, y);
            if (d < 0) {
                d = d + (4 * x) + 6;
            } else {
                d = d + (4 * (x - y)) + 10;
                y--;
            }
            x++;
        }
    }
    
    public void generateRandomSequence() {
        // Generador de números Random.
        // para la secuencia de What Does The Fox Say?
        Random r = new Random();
        for (int i = foxSequence.size(); i < difficulty; i++) {
            int pos = r.nextInt(5 - 0) + 0;  //Posición random entre 0 - 4.
            foxSequence.add(circles.get(pos)); //Secuencia para repetir. 
        }
        difficulty+=2;  //Se aumenta en dos (2) la dificultad.
    }
    
    public void showSequence() {
        isPlayerTurn = false;
        Circle c;
        //Muestra la secuencia escalando.
        for (int i = 0; i < foxSequence.size(); i++) {
            try{
                c = foxSequence.get(i); //Círculo de esa posición.
                //Se usa para detener por algunos segundos la pantalla.
                Thread.currentThread().sleep(FOX_TIME);
                isThePanelScaling = true;
                c = scale(c);
                circleToScale = c;
                repaint();
                Thread.currentThread().sleep(FOX_TIME);
                isThePanelScaling = false;
                repaint();
            }catch(InterruptedException ex) {
                Logger.getLogger(Practice1.class.getName()).log(Level.SEVERE, 
                                 null, ex);
            }
        }
        //Ya se le da la facultad al jugador para hacer click.
        isPlayerTurn = true;
    }
    
    public void translateToGamePosition(){
        for(int i = 1; i < circles.size(); i++){
            double delX = 0.0;
            double delY = 0.0;
            double sign;
            if(circles.get(i).xCenter != 0){ //Se modificaría xCenter.
                if(areCirclesExpanded){
                    sign = circles.get(i).xCenter > 0 ? -1.0 : 1.0;
                }else{
                    sign = circles.get(i).xCenter > 0 ? 1.0 : -1.0;
                }
                delX = (sign * TRANSLATE_FACTOR);
            }else{  //Se modificaría yCenter.
                if(areCirclesExpanded){
                    sign = circles.get(i).yCenter > 0 ? -1.0 : 1.0;
                }else{
                    sign = circles.get(i).yCenter > 0 ? 1.0 : -1.0;
                }
                delY = (sign * TRANSLATE_FACTOR);
            }
            Matrix2D m2D = new Matrix2D(delX, delY);
            Vector2D v2D = new Vector2D(circles.get(i).xCenter, 
                                        circles.get(i).yCenter, 1.0);
            //Se envía 1.0 como tercer parámetro para que solo se traslade 1
            //vez cada delta.
            
            Vector2D resultVector2D = Vector2D.multMat2DVect2D(m2D, v2D);
            circles.get(i).xCenter = resultVector2D.getCX();
            circles.get(i).yCenter = resultVector2D.getCY();
            //Es necesario setear el punto final para que se pueda pintar
            //correcta y dinámicamente la línea desde el centro hasta el borde
            //de la circunferencia.
            circles.get(i).setFinalPoint(resultVector2D.getCX(), 
                                         resultVector2D.getCY());
        } 
    }
    
    public Circle scale(Circle c){
        Matrix2D m2D = new Matrix2D(SCALE_FACTOR);
        //Se tiene el vector para multiplicar la matriz y aumentar el radio.
        Vector2D v2D = new Vector2D(c.xCenter, c.yCenter, c.radius);
        //Se tiene el vector con el círculo modificado.
        Vector2D resultVector2D = Vector2D.multMat2DVect2D(m2D, v2D);
        
        return new Circle(resultVector2D.getCX(), resultVector2D.getCY(), 
                          resultVector2D.getR(), c.color);
        
    }
    
    public Circle rotateCircle(Circle c, double rAngle){
        Circle rotatedCircle = new Circle(c.xCenter, c.yCenter, c.radius, 
                                          c.color);
        Vector2D auxiliarVector2D;
        double originalFinalPointX = rotatedCircle.finalPoint.getCX();
        double originalFinalPointY = rotatedCircle.finalPoint.getCY();
        
        //Se traslada la línea al centro.
        Matrix2D translatingM2D = new Matrix2D((-1)*originalFinalPointX, 
                                               (-1)*originalFinalPointY + 
                                                rotatedCircle.radius);
        
        auxiliarVector2D = Vector2D.multMat2DVect2D(translatingM2D, 
                                                      rotatedCircle.finalPoint);
        rotatedCircle.setFinalPoint(auxiliarVector2D.getCX(), 
                                 auxiliarVector2D.getCY()-rotatedCircle.radius);
        //Se rota la línea rAngle grados.
        Matrix2D m2D = new Matrix2D(rAngle, true);
        
        rotatedCircle.finalPoint = Vector2D.multMat2DVect2D(m2D, 
                                                      rotatedCircle.finalPoint);
        //Se traslada la línea a la ubicación original.
        translatingM2D = new Matrix2D(originalFinalPointX, 
                                               originalFinalPointY
                                               - rotatedCircle.radius);
        
        rotatedCircle.finalPoint = Vector2D.multMat2DVect2D(translatingM2D, 
                                                      rotatedCircle.finalPoint);
        
        return rotatedCircle;
        
    }
    
    public Circle isPointInsideCircle(double cX, double cY){
        for(int i = 0; i < circles.size(); i++){
            if(((cX - circles.get(i).xCenter)*(cX - circles.get(i).xCenter)) + 
               ((cY - circles.get(i).yCenter)*(cY - circles.get(i).yCenter)) < 
               (circles.get(i).radius*circles.get(i).radius)){
                return circles.get(i);
            }
        }
        return null;
    }
    
    public void compareSequences(){
        for(int i = 0; i < userSequence.size(); i++){
            if(userSequence.get(i) != foxSequence.get(i)){
                //El usuario falló repitiendo la secuencia.
                endGame();
                return;
            }
        }
        //El usuario aprobó la repetición de la secuencia completa y puede 
        //continuar jugando.
        if(userSequence.size() == foxSequence.size()){
            continueToNextSequence();
        }
        
    }
    
    public void endGame(){
        String incorrectSeq = "Secuencia Incorrecta. ¿Deseas volver a jugar?";
        String incorrectSeqTitle = "Error en la Secuencia";
        int userDecision = JOptionPane.showConfirmDialog(null, incorrectSeq, 
                                                         incorrectSeqTitle, 
                                                     JOptionPane.YES_NO_OPTION);
        //En caso de que el usuario desee volver a jugar.
        if(userDecision == JOptionPane.YES_OPTION){  
            isPlayerTurn = false;  //Se le acabó el turno al jugador.
            clickCounter = 0;
            userSequence.clear();
            foxSequence.clear();
            difficulty = 1;
            correctRounds = 0;
            isSequenceAlreadyGenerated = false;
            startRound();
        }else{
            System.exit(0);
        }
    }
    
    public void continueToNextSequence(){
        //Es necesario generar otra secuencia.
        correctRounds++;
        correctSeqNumber.setText(String.valueOf(correctRounds));
        isPlayerTurn = false;  //Se le acabó el turno al jugador.
        clickCounter = 0;
        userSequence.clear();
        isSequenceAlreadyGenerated = false;
        startRound();
    }
    
    // Mapeo coordenada Y para que pinte donde es.
    public int mapY(double y) {
        return (int) Math.round((y - (h / 2.0)) * -1.0);
    }
    
    // Mapeo coordenada X para que pinte donde es.
    public int mapX(double x) {
        return (int) Math.round(x + (w / 2.0));
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if(!isPlayerTurn) return;
        
        //En este caso el jugador estaría en turno.
        double x = (double) me.getX();
        double y = (double) me.getY();
        double mapX = x - (w / 2.0);
        double mapY = (y - (h / 2.0)) * -1.0;
        
        if(clickCounter < (difficulty - 2)){
            final Circle possibleCircle = isPointInsideCircle(mapX, mapY);
            if(possibleCircle != null){ //El punto clickeado está dentro de
                                        //un círculo.
                
                //Rota para indicarle al jugador cual botón presionó.
                Thread t2 = new Thread(new Runnable() {
                @Override
                public void run(){
                       
                    try{
                        //No se pone ningún Thread.sleep() para que se vea
                        //la rotación inmediatamente.
                        isPlayerTurn = false;
                        isTheCircleRotating = true;
                        circleToRotate = rotateCircle(possibleCircle, 
                                                      ROTATE_ANGLE);
                        repaint();
                        Thread.currentThread().sleep(FOX_TIME /2);
                        isTheCircleRotating = false;
                        repaint();
                        Thread.currentThread().sleep(FOX_TIME/2);
                        isTheCircleRotating = true;
                        circleToRotate = rotateCircle(possibleCircle,
                                                      (-1)*ROTATE_ANGLE);
                        repaint();
                        Thread.currentThread().sleep(FOX_TIME/2);
                        isTheCircleRotating = false;
                        repaint();
                        isPlayerTurn = true;
                    }catch(InterruptedException ex) {
                        Logger.getLogger(Practice1.class.getName()).
                                                    log(Level.SEVERE,null, ex);
                    }
                    
                }
                });  
                t2.start();
                userSequence.add(possibleCircle);
                //Se comprueba si el usuario se equivocó en cierta parte de la 
                //secuencia.
                if(userSequence.get(userSequence.size()-1) != 
                   foxSequence.get(userSequence.size()-1)){
                   endGame(); 
                   return;
                }
                clickCounter++;
                if(clickCounter == difficulty - 2){
                    //La secuencia es correcta.
                    continueToNextSequence();
                }
            }
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
    
    public static void main(String[] args) {
        // Crear un nuevo Frame
        JFrame frame = new JFrame("What Does The Fox Say?");
        // Al cerrar el frame, termina la ejecución de este programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Agregar un JPanel que se llama Points (esta clase)
        Practice1 p1 = new Practice1();
        JLabel correctSeqLabel = new JLabel("Secuencias Correctas: ");
        correctSeqLabel.setFont(correctSeqLabel.getFont().deriveFont(24f));
        p1.correctSeqNumber = new JLabel();
        p1.correctSeqNumber.setFont(
                                 p1.correctSeqNumber.getFont().deriveFont(24f));
        p1.add(correctSeqLabel);
        p1.add(p1.correctSeqNumber);
        frame.add(p1);
        // Asignarle tamaño
        frame.setSize(1024, 720);
        // Poner el frame en el centro de la pantalla
        frame.setLocationRelativeTo(null);
        // Mostrar el frame
        frame.setVisible(true);
    }
    
}
