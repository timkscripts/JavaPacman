/*
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
import java.awt.*;
import java.applet.Applet;
import java.awt.event.*;

/* 
 * This applet displays several images in a row.  It prevents
 * flashing by double buffering.  However, it doesn't wait until
 * the images are fully loaded before drawing them, which causes
 * the weird effect of the animation appearing from the top down.
 */

public class ImageSequence extends Applet implements Runnable, KeyListener {
  int frameNumber, delay;
  Thread   animatorThread;
  Graphics     offGraphics;
  Dimension    offDimension;
  
  Image offImage;
  
  Image pacman, pacmanLeft, pacmanUp, pacmanDown, wall, pellet, nopellet;   //all the images printed to the screen
  Image images[];                                                      //every image that is printed to the screen
  
  MediaTracker tracker;
  
  int pacmanX = 14, pacmanY = 23;                                     //pacmans location X and Y
  
  int paccount = 0;                                                    //the current frame of pacmans waka waka
  int animationspeed = 10;                                             //how fast pacman waka waka's
  
  int ghost1moveleft = 0, ghost1moveup = 0;
  int ghost1direction = 0;
  
  int ghost2moveleft = 0, ghost2moveup = 0;
  int ghost2direction = 0;
  
  
  int moveleft = 0, moveup = 0;                                        // direction ++ or --;
  int direction = 3;                                                   // what direction is pacman going
  
  int score = 0;
  
  boolean paccountB = true;                                            //Controls pacmans animation, > to - if true
  //- to > if false
  
  boolean orangeball = false;                                          //did you fire the orange portal?
  int pacXtemp = 0;                                                    //then give me pac's coords
  int pacYtemp = 0;
  int pacDirectionTemp = 0;                                            //ohyeah direction too
  int orangeDistance = 0;                                              //shoot that lazor
  int portalpoint = 0;                                                 //N S E or W?
  int orangeportalX = 10;
  int orangeportalY = 15;
  
  
  boolean blueball = false;                                          //did you fire the blue portal?
  int pacXtempB = 0;                                                    //then give me pac's coords
  int pacYtempB = 0;
  int pacDirectionTempB = 0;                                            //ohyeah direction too
  int blueDistance = 0;                                              //shoot that lazor
  int portalpointB = 0;                                                 //N S E or W?
  
  int blueportalX = 2;
  int blueportalY = 15;
  
  int startmenu = 0;                                                 //lets start the game
  int selection = 0;                                                //0 = New game 1 = options
  
  int ghost1Y = 12;
  int ghost1X = 15;
  
  
  int ghost2Y = 12;
  int ghost2X = 15;
  
  int pacQuadrant = 0;
  int pacRedChaseX = 0;
  int pacRedChaseY = 0;
  int pacPinkChaseX = 0;
  int pacPinkChaseY = 0;
  int tempRedDirection = 0;
  Image fileholder, fileholderfinal;                                   //fileholder holds a file and final is the
  //end result (transparent white colors)
   Color menuglow = new Color(1,1,130);
   int glowcount = 0;
  
  public static byte[][] board = new byte[][] {
    {-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12},
    {-12,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,-12},
    {-12,2,1,1,1,1,1,1,1,1,1,1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,1,1,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,2,2,2,1,2,2,1,2,2,2,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,-3,2,2,2,2,1,2,2,2,2,2,1,2,2,1,2,2,2,2,2,1,2,2,2,2,-3,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,2,2,2,1,2,2,1,2,2,2,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,1,2,2,2,2,2,2,2,2,1,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,1,2,2,2,2,2,2,2,2,1,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,1,1,1,1,1,1,2,2,1,1,1,1,2,2,1,1,1,1,2,2,1,1,1,1,1,1,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,2,2,2,0,2,2,0,2,2,2,2,2,1,2,2,2,2,2,2,-12}, 
    {-12,2,2,2,2,2,2,1,2,2,2,2,2,0,2,2,0,2,2,2,2,2,1,2,2,2,2,2,2,-12}, 
    {-12,2,2,2,2,2,2,1,2,2,0,0,0,0,0,0,0,0,0,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,2,2,2,2,2,2,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,0,5,6,7,8,0,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,0,0,0,0,0,0,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,2,2,2,2,2,2,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,0,0,0,0,0,0,0,0,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,2,2,2,2,2,2,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,2,2,2,2,2,1,2,2,0,2,2,2,2,2,2,2,2,0,2,2,1,2,2,2,2,2,2,-12},
    {-12,2,1,1,1,1,1,1,1,1,1,1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,1,1,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,2,2,2,1,2,2,1,2,2,2,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,1,2,2,2,2,1,2,2,2,2,2,1,2,2,1,2,2,2,2,2,1,2,2,2,2,1,2,-12},
    {-12,2,-3,1,1,2,2,1,1,1,1,1,1,1,0,-9,1,1,1,1,1,1,1,2,2,1,1,-3,2,-12},
    {-12,2,2,2,1,2,2,1,2,2,1,2,2,2,2,2,2,2,2,1,2,2,1,2,2,1,2,2,2,-12},
    {-12,2,2,2,1,2,2,1,2,2,1,2,2,2,2,2,2,2,2,1,2,2,1,2,2,1,2,2,2,-12},
    {-12,2,1,1,1,1,1,1,2,2,1,1,1,1,2,2,1,1,1,1,2,2,1,1,1,1,1,1,2,-12},
    {-12,2,1,2,2,2,2,2,2,2,2,2,2,1,2,2,1,2,2,2,2,2,2,2,2,2,2,1,2,-12},
    {-12,2,1,2,2,2,2,2,2,2,2,2,2,1,2,2,1,2,2,2,2,2,2,2,2,2,2,1,2,-12},
    {-12,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,-12},
    {-12,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,-12},
    {-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12,-12}
  };
  
  
  
  public void init() {
    String str;
    
    // How many milliseconds between frames?
    str = getParameter("fps");
    int fps = (str != null) ? Integer.parseInt(str) : 10;
    delay = (fps > 0) ? (100 / fps) : 100;
    
    // Load all the images.
    images = new Image[50];
    tracker = new MediaTracker(this);
    images[0] = getImage(getCodeBase(), "sprites\\nopellet.png");
    //images[1] = getImage(getCodeBase(), "sprites\\pac.gif");
    
    images[1] = imageinit("sprites\\circle.png"); //getImage(getCodeBase(), "sprites\\circle.png");
    images[2] = imageinit("sprites\\pellet.png");
    images[3] = imageinit("sprites\\wall.png");
    
    images[4] = imageinit("sprites\\left\\one.png");
    images[5] = imageinit("sprites\\left\\three.png");
    images[6] = imageinit("sprites\\left\\five.png");
    images[7] = imageinit("sprites\\left\\seven.png");
    
    
    
    images[8] = imageinit("sprites\\right\\one.png");
    images[9] = imageinit("sprites\\right\\three.png");
    images[10] = imageinit("sprites\\right\\five.png");
    images[11] = imageinit("sprites\\right\\seven.png");
    
    images[12] = imageinit("sprites\\up\\one.png");
    images[13] = imageinit("sprites\\up\\three.png");
    images[14] = imageinit("sprites\\up\\five.png");
    images[15] = imageinit("sprites\\up\\seven.png");
    
    
    images[16] = imageinit("sprites\\down\\one.png");
    images[17] = imageinit("sprites\\down\\three.png");
    images[18] = imageinit("sprites\\down\\five.png");
    images[19] = imageinit("sprites\\down\\seven.png");
    
    images[20] = imageinit("sprites\\pellet2.png");
    
    images[21] = imageinit("sprites\\portalgun.png");     //right
    images[22] = imageinit("sprites\\portalgunleft.png");
    images[23] = imageinit("sprites\\portalgunup.png");
    images[24] = imageinit("sprites\\portalgundown.png");
    
    images[25] = imageinit("sprites\\orangeball.png");
    images[26] = imageinit("sprites\\portalup.png");    //orange
    images[27] = imageinit("sprites\\portaldown.png");    //orange
    images[28] = imageinit("sprites\\portalleft.png");    //orange
    images[29] = imageinit("sprites\\portalright.png");    //orange
    
    images[30] = imageinit("sprites\\blueball.png");
    images[31] = imageinit("sprites\\portalBup.png");    //blue
    images[32] = imageinit("sprites\\portalBdown.png");    //blue
    images[33] = imageinit("sprites\\portalBleft.png");    //blue
    images[34] = imageinit("sprites\\portalBright.png");    //blue
   
    images[35] = imageinit("sprites\\Ghost1.png");     //darthvader ghosts
    images[36] = imageinit("sprites\\Ghost2.png");
    
    images[37] = imageinit("sprites\\Ghost3.png");    //mario ghosts
    images[38] = imageinit("sprites\\Ghost4.png");
    
    images[39] = imageinit("sprites\\GhostRed.png");    //mario ghosts
    images[40] = imageinit("sprites\\GhostPink.png");
    images[41] = imageinit("sprites\\GhostBlue.png");    //mario ghosts
    images[42] = imageinit("sprites\\GhostOrange.png");
    
    images[43] = null;//imageinit("sprites\\redblock.png");
        
        
    // images[8] = imageinit("sprites\\right\\five.png");
    // images[9] = imageinit("sprites\\scrap\\three.png");
    // images[10] = imageinit("sprites\\scrap\\one.png");
    tracker.addImage(images[0], 0);
    tracker.addImage(images[1], 0);
    tracker.addImage(images[2], 0);
    tracker.addImage(images[3], 0);
    addKeyListener(this);
  }
  
  public void start() {
    if (animatorThread == null) {
      animatorThread = new Thread(this);
      animatorThread.start();
    }
  }
  
  public void stop() {
    animatorThread = null;
    offImage = null;
    offGraphics = null;
  }
  
  public boolean mouseDown(Event e, int x, int y) {
    if (animatorThread == null) {
      start();
    }
    else {
      stop();
    }
    return false;
  }
  
  public void run() {
    // Remember the starting time
    long startTime = System.currentTimeMillis();
    
    while (Thread.currentThread() == animatorThread) {
      // Display the next frame of animation.
      repaint();
      
      // Delay depending on how far we are behind.
      try {
        startTime += delay;
        Thread.sleep(Math.max(0, 
                              startTime-System.currentTimeMillis()));
      } catch (InterruptedException e) {
        break;
      } 
      frameNumber++;
    }
  }
  
  // Paint the previous frame (if any).
  public void paint(Graphics g) {
    if (offImage != null) {
      g.drawImage(offImage, 0, 0, this);
    }
  }
  
  public void update(Graphics g) {
    Dimension d = size();
    // Create the offscreen graphics context, if no good one exists.
    if ( (offGraphics == null)
          || (d.width != offDimension.width)
          || (d.height != offDimension.height) ) {
      offDimension = d;
      offImage = createImage(d.width, d.height);
      offGraphics = offImage.getGraphics();
    }
    
    // Erase the previous image.
    offGraphics.setColor(Color.black);
    offGraphics.fillRect(0, 0, d.width, d.height);
    offGraphics.setColor(Color.black);
    
    //Paint the frame into the image.
    
    if (tracker.statusID(0, true) == MediaTracker.COMPLETE) {

      if (startmenu == 0)
      startmenu(g);
      else if (startmenu == 1){
      printBoard(g);
      printPacmanAndPortals(g);
      PacDirection();
      PacGhostRed(g);
      PacGhostPink(g);
      
      PacDeath();
      }
      else if (startmenu == 2)
      optionsmenu(g);
      else if (startmenu == 3)
      ChooseGhosts(g);
    } 
    // Paint the image onto the screen.

    g.drawImage(offImage, 0, 0, this);
    
    
    
    
  }
  
  public void PacDeath(){
    
    
    if (pacmanX == ghost1X && pacmanY == ghost1Y) 
      ResetLevel();
    
    if (pacmanX == ghost2X && pacmanY == ghost2Y) 
      ResetLevel();
    
  }
  
  
  public void ResetLevel(){
    
   
    pacmanX = 14;
    pacmanY = 23;                                     //pacmans location X and Y
    
    paccount = 0;                                                    //the current frame of pacmans waka waka
    animationspeed = 10;                                             //how fast pacman waka waka's
    
    ghost1moveleft = 0;
    ghost1moveup = 0;
    ghost1direction = 0;
    
    ghost2moveleft = 0;
    ghost2moveup = 0;
    ghost2direction = 0;
    
    
    moveleft = 0;
    moveup = 0;                                        // direction ++ or --;
    direction = 3;                                                   // what direction is pacman going
    
    score = 0;
    
    boolean paccountB = true;                                            //Controls pacmans animation, > to - if true
    //- to > if false
    
    boolean orangeball = false;                                          //did you fire the orange portal?
    pacXtemp = 0;                                                    //then give me pac's coords
    pacYtemp = 0;
    pacDirectionTemp = 0;                                            //ohyeah direction too
    orangeDistance = 0;                                              //shoot that lazor
    portalpoint = 0;                                                 //N S E or W?
    orangeportalX = 0;
    orangeportalY = 0;
    
    
    boolean blueball = false;                                          //did you fire the blue portal?
    pacXtempB = 0;                                                    //then give me pac's coords
    pacYtempB = 0;
    pacDirectionTempB = 0;                                            //ohyeah direction too
    blueDistance = 0;                                              //shoot that lazor
    portalpointB = 0;                                                 //N S E or W?
    
    blueportalX = 0;
    blueportalY = 0;
    
    // startmenu = 0;                                                 //lets start the game
    // selection = 0;                                                //0 = New game 1 = options
    
    ghost1Y = 12;
    ghost1X = 15;
    
    
    ghost2Y = 12;
    ghost2X = 15;
    
    pacQuadrant = 0;
    pacRedChaseX = 0;
    pacRedChaseY = 0;
    pacPinkChaseX = 0;
    pacPinkChaseY = 0;
    tempRedDirection = 0;
    
    
    
  }
  public void PacGhostRed(Graphics G){
    
    getQuadrant();
    
    
    offGraphics.drawImage(images[39], ghost1X * 15 + ghost1moveleft , ghost1Y * 15 + ghost1moveup , this);
    
    if (direction == 3){
    offGraphics.drawImage(images[43], (pacmanX-1) * 15 , pacmanY * 15 , this);
    pacRedChaseX = (pacmanX-5); //left
    pacRedChaseY = pacmanY;
    }
    else if (direction == 1){
    offGraphics.drawImage(images[43], (pacmanX+1)* 15 , pacmanY * 15 , this);
    pacRedChaseX = (pacmanX+5); //right
    pacRedChaseY = pacmanY;
    }
  else if (direction == 2){
    offGraphics.drawImage(images[43], pacmanX * 15 , (pacmanY+1) * 15, this);
    pacRedChaseY = (pacmanY+5); //down
    pacRedChaseX = (pacmanX);
    }
  else if (direction == 4){
    offGraphics.drawImage(images[43], pacmanX * 15 , (pacmanY-1) * 15 , this);
    pacRedChaseY = (pacmanY-5); //up
    pacRedChaseX = (pacmanX);
    }
    
  
  if (pacQuadrant >= 1){
   // if (ghost1moveup == 0 && ghost1moveleft == 0)
    double calculateme = 0;
    double calculateme2 = 0;
    double calculateme3 = 0;
    double calculateme4 = 0;
    double calculateme5 = 0;
    double calculateme6 = 0;
    double calculateme7 = 0;
    double calculateme8 = 0;
    double answer1 = 0;
    double answer2 = 0;
    double answer3 = 0;
    double answer4 = 0;
    
    if (board[ghost1Y][ghost1X+1] != 2){
      
      calculateme = pacRedChaseY - ghost1Y;
    calculateme2 = pacRedChaseX - ghost1X+1;
      calculateme = calculateme * calculateme;
      calculateme2 = calculateme2 * calculateme2;
      answer1 = calculateme + calculateme2;
      
    }
    if (board[ghost1Y][ghost1X-1] != 2){
      
      calculateme3 = pacRedChaseY - ghost1Y;
    calculateme4 = pacRedChaseX - ghost1X-1;
      calculateme3 = calculateme3 * calculateme3;
      calculateme4 = calculateme4 * calculateme4;
      answer2 = calculateme3 + calculateme4;
      
    }
    if (board[ghost1Y+1][ghost1X] != 2){
      
      calculateme5 = pacRedChaseY - ghost1Y+1;
    calculateme6 = pacRedChaseX - ghost1X;
      calculateme5 = calculateme5 * calculateme5;
      calculateme6 = calculateme6 * calculateme6;
      answer3 = calculateme5 + calculateme6;
      
    }
    if (board[ghost1Y-1][ghost1X] != 2){
      
      calculateme7 = pacRedChaseY - ghost1Y-1;
    calculateme8 = pacRedChaseX - ghost1X;
      calculateme7 = calculateme7 * calculateme7;
      calculateme8 = calculateme8 * calculateme8;
      answer4 = calculateme7 + calculateme8;
      
    }
    if (ghost1Y == 15 && ghost1X == 1)
      ghost1direction = 1;
    if (ghost1Y == 15 && ghost1X == 28)
      ghost1direction = 3;
    
    if (ghost1moveleft == 0 && ghost1moveup == 0)
      if (answer1 >= answer2 && answer1 >= answer3 && answer1 >= answer4){
      if (ghost1direction != 3)
      //if (board[ghost1Y][ghost1X+1] != 2)
      ghost1direction = 1;
      else if ( answer3 >= answer4 && board[ghost1Y+1][ghost1X] != 2)
      ghost1direction = 2;
      else  if (board[ghost1Y-1][ghost1X] != 2)
      ghost1direction = 4;
      
     if (board[ghost1Y][ghost1X+1] == 2 && board[ghost1Y-1][ghost1X] == 2 && board[ghost1Y+1][ghost1X] == 2)
       ghost1direction = 3;
      
    }
      
    else if (answer2 > answer1 && answer2 >= answer3 && answer2 >= answer4){
      if (ghost1direction != 1)
      ghost1direction = 3;
      else if ( answer3 >= answer4 && board[ghost1Y+1][ghost1X] != 2)
      ghost1direction = 2;
      else  if (board[ghost1Y-1][ghost1X] != 2)
      ghost1direction = 4;
      
      if (board[ghost1Y][ghost1X-1] == 2 && board[ghost1Y-1][ghost1X] == 2 && board[ghost1Y+1][ghost1X] == 2)
       ghost1direction = 1;
      
      
    } else if (answer3 > answer2 && answer3 > answer1 && answer3 >= answer4){
      if (ghost1direction != 4)
    // if (board[ghost1Y+1][ghost1X] != 2)
      ghost1direction = 2;
      else if ( answer1 >= answer2 && board[ghost1Y][ghost1X+1] != 2)
      ghost1direction = 1;
      else  if (board[ghost1Y][ghost1X-1] != 2)
      ghost1direction = 3;
      
      
     
   
    }else if (answer4 > answer2 && answer4 > answer1 && answer4 > answer3){
      if (ghost1direction != 2)
    // if (board[ghost1Y-1][ghost1X] != 2)
      ghost1direction = 4;
      else if ( answer2 >= answer1 && board[ghost1Y][ghost1X-1] != 2)
      ghost1direction = 3;
      else  if (board[ghost1Y][ghost1X+1] != 2)
      ghost1direction = 1;
      
      if (ghost1direction == 2){
       if ( board[ghost1Y][ghost1X-1] != 2)
         ghost1direction = 3;
       else if (board[ghost1Y][ghost1X+1] != 2)
         ghost1direction = 1;
         else if (board[ghost1Y-1][ghost1X] != 2)
         ghost1direction = 4;
        else if (board[ghost1Y+1][ghost1X] != 2)
         ghost1direction = 2;
        
      }
    }
  }
  Ghost1Direction();
 
  }
  
  
  
  public void PacGhostPink(Graphics G){
    
    getQuadrant();
    
    
    offGraphics.drawImage(images[40], ghost2X * 15 + ghost2moveleft , ghost2Y * 15 + ghost2moveup , this);
    
    if (direction == 3){
    offGraphics.drawImage(images[43], (pacmanX-1) * 15 , pacmanY * 15 , this);
    pacPinkChaseX = (pacmanX+5); //left
    pacPinkChaseY = pacmanY;
    }
    else if (direction == 1){
    offGraphics.drawImage(images[43], (pacmanX+1)* 15 , pacmanY * 15 , this);
    pacPinkChaseX = (pacmanX-5); //right
    pacPinkChaseY = pacmanY;
    }
  else if (direction == 2){
    offGraphics.drawImage(images[43], pacmanX * 15 , (pacmanY+1) * 15, this);
    pacPinkChaseY = (pacmanY-5); //down
    pacPinkChaseX = (pacmanX);
    }
  else if (direction == 4){
    offGraphics.drawImage(images[43], pacmanX * 15 , (pacmanY-1) * 15 , this);
    pacPinkChaseY = (pacmanY+5); //up
    pacPinkChaseX = (pacmanX);
    }
    
  
  if (pacQuadrant >= 1){
   // if (ghost2moveup == 0 && ghost2moveleft == 0)
    double calculateme = 0;
    double calculateme2 = 0;
    double calculateme3 = 0;
    double calculateme4 = 0;
    double calculateme5 = 0;
    double calculateme6 = 0;
    double calculateme7 = 0;
    double calculateme8 = 0;
    double answer1 = 0;
    double answer2 = 0;
    double answer3 = 0;
    double answer4 = 0;
    
    if (board[ghost2Y][ghost2X+1] != 2){
      
      calculateme = pacPinkChaseY - ghost2Y;
    calculateme2 = pacPinkChaseX - ghost2X+1;
      calculateme = calculateme * calculateme;
      calculateme2 = calculateme2 * calculateme2;
      answer1 = calculateme + calculateme2;
      
    }
    if (board[ghost2Y][ghost2X-1] != 2){
      
      calculateme3 = pacPinkChaseY - ghost2Y;
    calculateme4 = pacPinkChaseX - ghost2X-1;
      calculateme3 = calculateme3 * calculateme3;
      calculateme4 = calculateme4 * calculateme4;
      answer2 = calculateme3 + calculateme4;
      
    }
    if (board[ghost2Y+1][ghost2X] != 2){
      
      calculateme5 = pacPinkChaseY - ghost2Y+1;
    calculateme6 = pacPinkChaseX - ghost2X;
      calculateme5 = calculateme5 * calculateme5;
      calculateme6 = calculateme6 * calculateme6;
      answer3 = calculateme5 + calculateme6;
      
    }
    if (board[ghost2Y-1][ghost2X] != 2){
      
      calculateme7 = pacPinkChaseY - ghost2Y-1;
    calculateme8 = pacPinkChaseX - ghost2X;
      calculateme7 = calculateme7 * calculateme7;
      calculateme8 = calculateme8 * calculateme8;
      answer4 = calculateme7 + calculateme8;
      
    }
    if (ghost2Y == 15 && ghost2X == 1)
      ghost2direction = 1;
    if (ghost2Y == 15 && ghost2X == 28)
      ghost2direction = 3;
    
    if (ghost2moveleft == 0 && ghost2moveup == 0)
      if (answer1 >= answer2 && answer1 >= answer3 && answer1 >= answer4){
      if (ghost2direction != 3)
      //if (board[ghost2Y][ghost2X+1] != 2)
      ghost2direction = 1;
      else if ( answer3 >= answer4 && board[ghost2Y+1][ghost2X] != 2)
      ghost2direction = 2;
      else  if (board[ghost2Y-1][ghost2X] != 2)
      ghost2direction = 4;
      
     if (board[ghost2Y][ghost2X+1] == 2 && board[ghost2Y-1][ghost2X] == 2 && board[ghost2Y+1][ghost2X] == 2)
       ghost2direction = 3;
      
    }
      
    else if (answer2 > answer1 && answer2 >= answer3 && answer2 >= answer4){
      if (ghost2direction != 1)
      ghost2direction = 3;
      else if ( answer3 >= answer4 && board[ghost2Y+1][ghost2X] != 2)
      ghost2direction = 2;
      else  if (board[ghost2Y-1][ghost2X] != 2)
      ghost2direction = 4;
      
      if (board[ghost2Y][ghost2X-1] == 2 && board[ghost2Y-1][ghost2X] == 2 && board[ghost2Y+1][ghost2X] == 2)
       ghost2direction = 1;
      
      
    } else if (answer3 > answer2 && answer3 > answer1 && answer3 >= answer4){
      if (ghost2direction != 4)
    // if (board[ghost2Y+1][ghost2X] != 2)
      ghost2direction = 2;
      else if ( answer1 >= answer2 && board[ghost2Y][ghost2X+1] != 2)
      ghost2direction = 1;
      else  if (board[ghost2Y][ghost2X-1] != 2)
      ghost2direction = 3;
      
      
     
   
    }else if (answer4 > answer2 && answer4 > answer1 && answer4 > answer3){
      if (ghost2direction != 2)
    // if (board[ghost2Y-1][ghost2X] != 2)
      ghost2direction = 4;
      else if ( answer2 >= answer1 && board[ghost2Y][ghost2X-1] != 2)
      ghost2direction = 3;
      else  if (board[ghost2Y][ghost2X+1] != 2)
      ghost2direction = 1;
      
      if (ghost2direction == 2){
       if ( board[ghost2Y][ghost2X-1] != 2)
         ghost2direction = 3;
       else if (board[ghost2Y][ghost2X+1] != 2)
         ghost2direction = 1;
         else if (board[ghost2Y-1][ghost2X] != 2)
         ghost2direction = 4;
        else if (board[ghost2Y+1][ghost2X] != 2)
         ghost2direction = 2;
        
      }
    }
  }
  Ghost2Direction();
 
  }
  
  public void getQuadrant(){
    
    if (pacmanX <= 14 && pacmanY <= 14)
      pacQuadrant = 2;
    else if (pacmanX > 14 && pacmanY > 14)
      pacQuadrant = 4;
    else if (pacmanX <= 14 && pacmanY > 14)
      pacQuadrant = 3;
    else if (pacmanX > 14 && pacmanY <=14)
      pacQuadrant = 1;
  }
  
  public void ChooseGhosts(Graphics g){
      offGraphics.setColor(Color.yellow);
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 40)  );
    offGraphics.drawString( "Choose ghosts" , 1, 40);
   
    offGraphics.drawString( "<<<<" , 190, 100 + (selection * 15) - 80);
    offGraphics.drawImage(images[35], 1 , 80 , this); 
    offGraphics.drawImage(images[36], 17 , 80 , this); 
    offGraphics.drawImage(images[36], 33 , 80 , this);
    offGraphics.drawImage(images[36], 49 , 80 , this);
    
    offGraphics.drawImage(images[37], 1 , 100 , this); 
    offGraphics.drawImage(images[38], 17 , 100 , this); 
    offGraphics.drawImage(images[38], 33 , 100 , this);
    offGraphics.drawImage(images[38], 49 , 100 , this);
  }
  
  public void optionsmenu(Graphics g){
    
    offGraphics.setColor(Color.yellow);
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 40)  );
    offGraphics.drawString( "Options" , 1, 40);
    if (selection == 2)
      offGraphics.setColor(menuglow);
    else 
      offGraphics.setColor(Color.white); 
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
    offGraphics.drawString( "Choose ghosts" , 1, 80);
    if (selection == 3)
      offGraphics.setColor(menuglow);
    else 
      offGraphics.setColor(Color.white);
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
    offGraphics.drawString( "Choose music" , 1, 120);
    if (selection == 4)
      offGraphics.setColor(menuglow);
    else 
      offGraphics.setColor(Color.white);
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
    offGraphics.drawString( "Choose graphics" , 1, 150);
    if (selection == 5)
      offGraphics.setColor(menuglow);
    else 
      offGraphics.setColor(Color.white);
    offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
    offGraphics.drawString( "Back" , 1, 180);
    
    glowcount++;
    if (glowcount >= 250)
      glowcount = 1;
    menuglow = new Color(glowcount,glowcount,130);
    
  }
  public void startmenu(Graphics g){
    
      offGraphics.setColor(Color.yellow);
      offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 40)  );
      offGraphics.drawString( "PAC MAN" , 125, 180);
      if (selection == 0)
      offGraphics.setColor(menuglow);
      else 
      offGraphics.setColor(Color.white); 
      offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
      offGraphics.drawString( "NEW GAME" , 130, 220);
      if (selection == 1)
      offGraphics.setColor(menuglow);
      else 
      offGraphics.setColor(Color.white);
      offGraphics.setFont( new Font ("Helvetica", Font.BOLD, 30)  );
      offGraphics.drawString( "OPTIONS" , 145, 260);
      
      glowcount++;
      if (glowcount >= 250)
      glowcount = 1;
      menuglow = new Color(glowcount,glowcount,130);
    
  }
  public void printPacmanAndPortals(Graphics g){
    
         if (direction == 3)
        printpacLeft(g);
      else if (direction == 1)
        printpacRight(g);
      else if (direction == 4)
        printpacUp(g);
      else if (direction == 2)
        printpacDown(g);
      
      if (orangeball == true )
        orangeanimation();
      
      if (blueball == true )
        blueanimation();
      offGraphics.setColor(Color.white);
      offGraphics.drawString( Integer.toString(pacQuadrant) , 460, 42);
      
      if (portalpoint == 4)
      offGraphics.drawImage(images[26], orangeportalX * 15 , orangeportalY * 15 , this); 
      if (portalpoint == 3)
      offGraphics.drawImage(images[29], orangeportalX * 15 , orangeportalY * 15 , this); 
      if (portalpoint == 2)
      offGraphics.drawImage(images[27], orangeportalX * 15 , orangeportalY * 15 , this); 
      if (portalpoint == 1)
      offGraphics.drawImage(images[28], orangeportalX * 15 , orangeportalY * 15 , this); 
      
      if (portalpointB == 4)
      offGraphics.drawImage(images[31], blueportalX * 15 , blueportalY * 15 , this); 
      if (portalpointB == 3)
      offGraphics.drawImage(images[34], blueportalX * 15 , blueportalY * 15 , this); 
      if (portalpointB == 2)
      offGraphics.drawImage(images[32], blueportalX * 15 , blueportalY * 15 , this); 
      if (portalpointB == 1)
      offGraphics.drawImage(images[33], blueportalX * 15 , blueportalY * 15 , this);  
    
    
  }
  public void orangeanimation(){
    if (pacDirectionTemp == 1)
     offGraphics.drawImage(images[25], pacXtemp * 15 + orangeDistance + 15, pacYtemp * 15 + 5, this); 
    else if (pacDirectionTemp == 3)
     offGraphics.drawImage(images[25], pacXtemp * 15 - orangeDistance - 15, pacYtemp * 15 + 5, this); 
    if (pacDirectionTemp == 4)
      offGraphics.drawImage(images[25], pacXtemp * 15 , pacYtemp * 15  - orangeDistance, this); 
    if (pacDirectionTemp == 2)
      offGraphics.drawImage(images[25], pacXtemp * 15 , pacYtemp * 15 + orangeDistance, this); 
    
    orangeDistance += 5;
    
    if (pacDirectionTemp == 1)
      if (board[pacYtemp][pacXtemp + (orangeDistance/15)] == 2){
      orangeball = false;
      orangeportalY = pacYtemp;
      orangeportalX = pacXtemp + (orangeDistance/15);
      portalpoint = 1;
    }
    if (pacDirectionTemp == 3)
      if (board[pacYtemp][pacXtemp - (orangeDistance/15)] == 2){
      orangeball = false;
      orangeportalY = pacYtemp;
      orangeportalX = pacXtemp - (orangeDistance/15);
      portalpoint = 3;
    }
    if (pacDirectionTemp == 4)
      if (board[pacYtemp - (orangeDistance/15)][pacXtemp] == 2){
      orangeball = false; 
      orangeportalX = pacXtemp;
      orangeportalY = pacYtemp - (orangeDistance/15);
      portalpoint = 4;
      }
     if (pacDirectionTemp == 2)
       if (board[pacYtemp + (orangeDistance/15)][pacXtemp] == 2){
       orangeball = false; 
       orangeportalX = pacXtemp;
       orangeportalY = pacYtemp + (orangeDistance/15);
       portalpoint = 2;
       }
     if (orangeball == false){
       
       orangeDistance = 0;
       pacYtemp = 0;
       pacXtemp = 0;
     }
     
  }
  
  
   public void blueanimation(){
    if (pacDirectionTempB == 1)
     offGraphics.drawImage(images[30], pacXtempB * 15 + blueDistance + 15, pacYtempB * 15 + 5, this); 
    else if (pacDirectionTempB == 3)
     offGraphics.drawImage(images[30], pacXtempB * 15 - blueDistance - 15, pacYtempB * 15 + 5, this); 
    if (pacDirectionTempB == 4)
      offGraphics.drawImage(images[30], pacXtempB * 15 , pacYtempB * 15  - blueDistance, this); 
    if (pacDirectionTempB == 2)
      offGraphics.drawImage(images[30], pacXtempB * 15 , pacYtempB * 15 + blueDistance, this); 
    
    blueDistance += 5;
    
    if (pacDirectionTempB == 1)
      if (board[pacYtempB][pacXtempB + (blueDistance/15)] == 2){
      blueball = false;
      blueportalY = pacYtempB;
      blueportalX = pacXtempB + (blueDistance/15);
      portalpointB = 1;
    }
    if (pacDirectionTempB == 3)
      if (board[pacYtempB][pacXtempB - (blueDistance/15)] == 2){
      blueball = false;
      blueportalY = pacYtempB;
      blueportalX = pacXtempB - (blueDistance/15);
      portalpointB = 3;
    }
    if (pacDirectionTempB == 4)
      if (board[pacYtempB - (blueDistance/15)][pacXtempB] == 2){
      blueball = false; 
      blueportalX = pacXtempB;
      blueportalY = pacYtempB - (blueDistance/15);
      portalpointB = 4;
      }
     if (pacDirectionTempB == 2)
       if (board[pacYtempB + (blueDistance/15)][pacXtempB] == 2){
       blueball = false; 
       blueportalX = pacXtempB;
       blueportalY = pacYtempB + (blueDistance/15);
       portalpointB = 2;
       }
     if (blueball == false){
       
       blueDistance = 0;
       pacYtempB = 0;
       pacXtempB = 0;
     }
     
  }
   
   public void orangecheck() {
     if (board[orangeportalY + 1][orangeportalX] != 2 && board[orangeportalY + 1][orangeportalX] != -12){
       direction = 2;
       if (board[orangeportalY + 1][orangeportalX] == 1)
         score += 10;
       else if (board[orangeportalY + 1][orangeportalX] == -3)
         score += 50;
       pacmanY = orangeportalY+1;
       pacmanX = orangeportalX;
     } else if (board[orangeportalY - 1][orangeportalX] != 2 && board[orangeportalY - 1][orangeportalX] != -12){
       direction = 4;
       if (board[orangeportalY - 1][orangeportalX] == 1)
         score += 10;
       else if (board[orangeportalY - 1][orangeportalX] == -3)
         score += 50;
       pacmanY = orangeportalY-1;
       pacmanX = orangeportalX;
     } else if (board[orangeportalY][orangeportalX+1] != 2 && board[orangeportalY][orangeportalX+1] != -12){
       direction = 1;
       if (board[orangeportalY][orangeportalX + 1] == 1)
         score += 10;
       else if (board[orangeportalY][orangeportalX + 1] == -3)
         score += 50;
       pacmanY = orangeportalY;
       pacmanX = orangeportalX+1;
     }  else if (board[orangeportalY][orangeportalX-1] != 2 && board[orangeportalY ][orangeportalX-1] != -12){
       direction = 3;
       if (board[orangeportalY][orangeportalX - 1] == 1)
         score += 10;
       else if (board[orangeportalY][orangeportalX - 1] == -3)
         score += 50;
       pacmanY = orangeportalY;
       pacmanX = orangeportalX-1;
     } 
     
     
   }
   
     public void bluecheck() {
     if (board[blueportalY + 1][blueportalX] != 2 && board[blueportalY + 1][blueportalX] != -12){
       direction = 2;
       if (board[blueportalY + 1][blueportalX] == 1)
         score += 10;
       else if (board[blueportalY + 1][blueportalX] == -3)
         score += 50;
       pacmanY = blueportalY+1;
       pacmanX = blueportalX;
     } else if (board[blueportalY - 1][blueportalX] != 2 && board[blueportalY - 1][blueportalX] != -12){
       if (board[blueportalY - 1][blueportalX] == 1)
         score += 10;
       else if (board[blueportalY - 1][blueportalX] == -3)
         score += 50;
       direction = 4;
       pacmanY = blueportalY-1;
       pacmanX = blueportalX;
     } else if (board[blueportalY][blueportalX+1] != 2 && board[blueportalY ][blueportalX+1] != -12){
       if (board[blueportalY][blueportalX+1] == 1)
         score += 10;
       else if (board[blueportalY][blueportalX+1] == -3)
         score += 50;
       direction = 1;
       pacmanY = blueportalY;
       pacmanX = blueportalX+1;
     }  else if (board[blueportalY][blueportalX-1] != 2 && board[blueportalY][blueportalX-1] != -12){
       if (board[blueportalY][blueportalX - 1] == 1)
         score += 10;
       else if  (board[blueportalY][blueportalX - 1] == -3)
         score += 50;
       direction = 3;
       pacmanY = blueportalY;
       pacmanX = blueportalX-1;
     } 
     
     
   }
     
       public void Ghost1Direction(){
    
    if (ghost1direction == 3){
     // if (board[pacmanY][pacmanX-1] != 2)
        ghost1moveleft--;
      if (ghost1moveleft <= -7){
        
       
        ghost1X--;
        ghost1moveleft = 8;
      }
      if (ghost1moveleft > 0)
        ghost1moveleft--;
    }
    else if (ghost1direction == 1){
     // if (board[pacmanY][pacmanX+1] != 2)
        ghost1moveleft++;
      if (ghost1moveleft >= 7){
       
   
        
        ghost1X++;
        ghost1moveleft = -8;
      }
      if (ghost1moveleft < 0)
        ghost1moveleft++;
    } else if (ghost1direction == 2){
      //if (board[pacmanY+1][pacmanX] != 2)
        ghost1moveup++;
      if (ghost1moveup >= 7){
        
      ghost1Y++;
      ghost1moveup = -8;
      }
      if (ghost1moveup < 0)
        ghost1moveup++;
    }
    else if (ghost1direction == 4){
      //if (board[pacmanY-1][pacmanX] != 2)
        ghost1moveup--;
      if (ghost1moveup <= -7){
        
        ghost1Y--;
        ghost1moveup = 8;
      }
      if (ghost1moveup > 0)
        ghost1moveup--;
    }
    
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       
        public void Ghost2Direction(){
    
    if (ghost2direction == 3){
     // if (board[pacmanY][pacmanX-1] != 2)
        ghost2moveleft--;
      if (ghost2moveleft <= -7){
        
       
        ghost2X--;
        ghost2moveleft = 8;
      }
      if (ghost2moveleft > 0)
        ghost2moveleft--;
    }
    else if (ghost2direction == 1){
     // if (board[pacmanY][pacmanX+1] != 2)
        ghost2moveleft++;
      if (ghost2moveleft >= 7){
       
   
        
        ghost2X++;
        ghost2moveleft = -8;
      }
      if (ghost2moveleft < 0)
        ghost2moveleft++;
    } else if (ghost2direction == 2){
      //if (board[pacmanY+1][pacmanX] != 2)
        ghost2moveup++;
      if (ghost2moveup >= 7){
        
      ghost2Y++;
      ghost2moveup = -8;
      }
      if (ghost2moveup < 0)
        ghost2moveup++;
    }
    else if (ghost2direction == 4){
      //if (board[pacmanY-1][pacmanX] != 2)
        ghost2moveup--;
      if (ghost2moveup <= -7){
        
        ghost2Y--;
        ghost2moveup = 8;
      }
      if (ghost2moveup > 0)
        ghost2moveup--;
    }
    
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void PacDirection(){
    
    if (direction == 3){
      if (board[pacmanY][pacmanX-1] != 2)
        moveleft--;
      if (moveleft <= -7){
        if (board[pacmanY][pacmanX-1] == 1)
          score += 10;
        else if (board[pacmanY][pacmanX-1] == -3)
          score += 50;

        

        board[pacmanY][pacmanX] = 0;
        
       
        if (pacmanY == 14 && pacmanX == 1)
          pacmanX = 26;
        board[pacmanY][pacmanX-1] = -9;
        if ((pacmanX - 2) == blueportalX && pacmanY == blueportalY ){
          if (board[pacmanY][pacmanX-1] == 1)
            score += 10;
          else if (board[pacmanY][pacmanX-1] == -3)
            score += 50;
          board[pacmanY][pacmanX-1] = 0;
          orangecheck();
          
        }
        if ((pacmanX - 2) == orangeportalX && pacmanY == orangeportalY){
          if (board[pacmanY][pacmanX-1] == 1)
            score += 10;
          else if (board[pacmanY][pacmanX-1] == -3)
            score += 50;
          
          board[pacmanY][pacmanX-1] = 0;
          bluecheck();
          
          
        }
        moveleft = 8;
      }
      if (moveleft > 0)
        moveleft--;
    }
    else if (direction == 1){
      if (board[pacmanY][pacmanX+1] != 2)
        moveleft++;
      if (moveleft >= 7){
        if (board[pacmanY][pacmanX+1] == 1)
          score += 10;
        else if (board[pacmanY][pacmanX+1] == -3)
          score += 50;
        
        board[pacmanY][pacmanX] = 0;
        
        if (pacmanY == 14 && pacmanX == 26)
          pacmanX = 0;
        
        board[pacmanY][pacmanX+1] = -9;
        
        
             if ((pacmanX + 2) == blueportalX && pacmanY == blueportalY ){
          if (board[pacmanY][pacmanX+1] == 1)
            score += 10;
          else if (board[pacmanY][pacmanX+1] == -3)
            score += 50;
          board[pacmanY][pacmanX+1] = 0;
          orangecheck();
          
        }
        if ((pacmanX + 2) == orangeportalX && pacmanY == orangeportalY){
          if (board[pacmanY][pacmanX+1] == 1)
            score += 10;
          else if (board[pacmanY][pacmanX+1] == -3)
            score += 50;
          
          board[pacmanY][pacmanX+1] = 0;
          bluecheck();
          
          
        }
        
        moveleft = -8;
      }
      if (moveleft < 0)
        moveleft++;
    } else if (direction == 2){
      if (board[pacmanY+1][pacmanX] != 2)
        moveup++;
      if (moveup >= 7){
        if (board[pacmanY+1][pacmanX] == 1)
          score += 10;
        else if (board[pacmanY+1][pacmanX] == -3)
          score += 50;
        board[pacmanY][pacmanX] = 0;
        board[pacmanY+1][pacmanX] = -9;
        
     if ((pacmanX ) == blueportalX && (pacmanY + 2) == blueportalY ){
          if (board[pacmanY+1][pacmanX] == 1)
            score += 10;
          else if (board[pacmanY+1][pacmanX] == -3)
            score += 50;
          board[pacmanY+1][pacmanX] = 0;
          orangecheck();
          
        }
        if ((pacmanX) == orangeportalX && (pacmanY + 2) == orangeportalY){
          if (board[pacmanY+1][pacmanX] == 1)
            score += 10;
          else if (board[pacmanY+1][pacmanX] == -3)
            score += 50;
          
          board[pacmanY+1][pacmanX] = 0;
          bluecheck();
          
          
        }
        
        moveup = -8;
      }
      if (moveup < 0)
        moveup++;
    }
    else if (direction == 4){
      if (board[pacmanY-1][pacmanX] != 2)
        moveup--;
      if (moveup <= -7){
        if (board[pacmanY-1][pacmanX] == 1)
          score += 10;
        else if (board[pacmanY-1][pacmanX] == -3)
          score += 50;
        board[pacmanY][pacmanX] = 0;
        board[pacmanY-1][pacmanX] = -9;
        
         if ((pacmanX ) == blueportalX && (pacmanY - 2) == blueportalY ){
          if (board[pacmanY-1][pacmanX] == 1)
            score += 10;
          else if (board[pacmanY-1][pacmanX] == -3)
            score += 50;
          board[pacmanY-1][pacmanX] = 0;
          orangecheck();
          
        }
        if ((pacmanX) == orangeportalX && (pacmanY - 2) == orangeportalY){
          if (board[pacmanY-1][pacmanX] == 1)
            score += 10;
          else if (board[pacmanY-1][pacmanX] == -3)
            score += 50;
          
          board[pacmanY-1][pacmanX] = 0;
          bluecheck();
          
          
        }
        
        
        moveup = 8;
      }
      if (moveup > 0)
        moveup--;
    }
    
  }
  
  public Image imageinit(String filename)
  {
    MediaTracker media = new MediaTracker(this);
    
    fileholder = 
      getImage(getDocumentBase(),filename);
    media.addImage(fileholder,0);
    try {
      media.waitForID(0);
      
      fileholderfinal = 
        Transparency.makeColorTransparent
        (fileholder, new Color(0).white);
      
    } 
    catch(InterruptedException e) {}
    return fileholderfinal;
    
    
  }
  
  
  public void printBoard(Graphics g) {
    
    for(byte e=0; e<33; e++){
      for(byte i=0; i<29; i++){
        if (board[e][i] == 1){
          // g.drawImage(nopellet,i*15, e*15,this); 
          // g.drawImage(pellet,i*15 + 6, e*15 + 6,this);
          offGraphics.drawImage(images[0], i*15, e*15, this); 
          offGraphics.drawImage(images[2], i*15 + 6, e*15 + 6, this); 
        }else if (board[e][i] == -9){
          pacmanY = e;
          pacmanX = i;
          
          offGraphics.drawImage(images[0], i*15, e*15, this); 
          
        } else if (board[e][i] == 2){
          //g.drawImage(nopellet,i*15, e*15,this); 
          // g.drawImage(wall,i*15, e*15,this);   
          offGraphics.drawImage(images[3], i*15, e*15, this); 
          
        } else if (board[e][i] == 0){
          
          offGraphics.drawImage(images[0], i*15, e*15, this); 
        } else if (board[e][i] == -3){
          offGraphics.drawImage(images[0], i*15, e*15, this); 
          offGraphics.drawImage(images[20], i*15, e*15, this); 
        } else if (board[e][i] == -12){
          
          offGraphics.drawImage(images[1], i*15, e*15, this); 
        } 
      }
    }         
    
  }
  
  public void printpacLeft(Graphics g) {
    moveup = 0;
    offGraphics.drawImage(images[22], pacmanX*15 + moveleft - 13 , pacmanY*15 + moveup + 5, this);
    if (paccount == 0 ){
      offGraphics.drawImage(images[4], pacmanX*15 + moveleft, pacmanY*15, this);
      if (paccountB == false)
        paccountB = true;
    }
    else if (paccount <= 1*animationspeed)
      offGraphics.drawImage(images[5], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 2*animationspeed)
      offGraphics.drawImage(images[6], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 3*animationspeed)
      offGraphics.drawImage(images[7], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 4*animationspeed){
      offGraphics.drawImage(images[7], pacmanX*15 + moveleft, pacmanY*15, this);
      if (paccountB == true)
        paccountB = false;
    } 
    if (paccountB == true)
      paccount++;
    else
      paccount--;
    
  }
  
  public void printpacRight(Graphics g) {
    moveup = 0;
    
    if (paccount == 0 ){
      offGraphics.drawImage(images[8], pacmanX*15 + moveleft, pacmanY*15, this);
      if (paccountB == false)
        paccountB = true;
    }
    else if (paccount <= 1*animationspeed)
      offGraphics.drawImage(images[9], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 2*animationspeed)
      offGraphics.drawImage(images[10], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 3*animationspeed)
      offGraphics.drawImage(images[11], pacmanX*15 + moveleft, pacmanY*15, this);
    else if (paccount <= 4*animationspeed){
      offGraphics.drawImage(images[11], pacmanX*15 + moveleft, pacmanY*15, this);
      if (paccountB == true)
        paccountB = false;
    } 
    if (paccountB == true)
      paccount++;
    else
      paccount--;
    
    offGraphics.drawImage(images[21], pacmanX*15 + moveleft , pacmanY*15 + moveup + 5, this);
  }
  
  public void printpacUp(Graphics g) {
    moveleft = 0;
    if (paccount == 0 ){
      offGraphics.drawImage(images[12], pacmanX*15 , pacmanY*15 + moveup, this);
      if (paccountB == false)
        paccountB = true;
    }
    else if (paccount <= 1*animationspeed)
      offGraphics.drawImage(images[13], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 2*animationspeed)
      offGraphics.drawImage(images[14], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 3*animationspeed)
      offGraphics.drawImage(images[15], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 4*animationspeed){
      offGraphics.drawImage(images[15], pacmanX*15 , pacmanY*15 + moveup, this);
      if (paccountB == true)
        paccountB = false;
    } 
    if (paccountB == true)
      paccount++;
    else
      paccount--;
    offGraphics.drawImage(images[23], pacmanX*15 + moveleft - 3 , pacmanY*15 + moveup - 15, this);
  }
  
  
  public void printpacDown(Graphics g) {
    moveleft = 0;          
    offGraphics.drawImage(images[24], pacmanX*15 + moveleft - 3 , pacmanY*15 + moveup, this);
    if (paccount == 0 ){
      offGraphics.drawImage(images[16], pacmanX*15 , pacmanY*15 + moveup, this);
      if (paccountB == false)
        paccountB = true;
    }
    else if (paccount <= 1*animationspeed)
      offGraphics.drawImage(images[17], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 2*animationspeed)
      offGraphics.drawImage(images[18], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 3*animationspeed)
      offGraphics.drawImage(images[19], pacmanX*15 , pacmanY*15 + moveup, this);
    else if (paccount <= 4*animationspeed){
      offGraphics.drawImage(images[19], pacmanX*15 , pacmanY*15 + moveup, this);
      if (paccountB == true)
        paccountB = false;
    } 
    if (paccountB == true)
      paccount++;
    else
      paccount--;
    
  }
  
  
  public void keyPressed( KeyEvent e ) { 
    
    switch (e.getKeyCode()) { 
      case KeyEvent.VK_LEFT:  
        if (startmenu == 0)
        break;
        if (board[pacmanY][pacmanX-1] != 2 && moveup > -7 && moveup < 7 && moveleft < 7 && moveleft > -7){
        direction = 3;
        moveup = 0;
      }
        break;
        
      case KeyEvent.VK_RIGHT:
        if (startmenu == 0)
        break;
        if (board[pacmanY][pacmanX+1] != 2  && moveup > -7 && moveup < 7 && moveleft < 7 && moveleft > -7){
        direction = 1;
        moveup = 0;
      }
        break;
        
      case KeyEvent.VK_UP:  
        if (startmenu == 0){
        if (selection == 0)
        selection = 1;
        else 
        selection = 0;
      } else if (startmenu == 2){
        if (selection == 2)
        selection = 5;
        else if (selection == 3)
        selection = 2;
        else if (selection == 4)
        selection = 3;
        else if (selection == 5)
        selection = 4;
        
      } else if (startmenu == 3){
        if (selection == 6)
        selection = 9;
        else if (selection == 7)
        selection = 6;
        else if (selection == 8)
        selection = 7;
        else if (selection == 9)
        selection = 8;
        
      } 
        
        
        if (startmenu == 0)
        break;
        if (board[pacmanY-1][pacmanX] != 2 && moveup > -7 && moveup < 7 && moveleft < 7 && moveleft > -7){
        direction = 4;
        moveleft = 0;
      }
        break;
        
      case KeyEvent.VK_DOWN: 
         if (startmenu == 0){
        if (selection == 0)
        selection = 1;
        else 
        selection = 0;
      } else if (startmenu == 2){
        if (selection == 2)
        selection = 3;
        else if (selection == 3)
        selection = 4;
        else if (selection == 4)
        selection = 5;
        else if (selection == 5)
        selection = 2;
        
      } else if (startmenu == 3){
        if (selection == 6)
        selection = 7;
        else if (selection == 7)
        selection = 8;
        else if (selection == 8)
        selection = 9;
        else if (selection == 9)
        selection = 6;
        
      }
        if (startmenu == 0)
        break;
        if (board[pacmanY+1][pacmanX] != 2 && moveup > -7 && moveup < 7 && moveleft < 7 && moveleft > -7){
        direction = 2;
        moveleft = 0;
      }
        break;
        
      case KeyEvent.VK_Z: 
        if (orangeball != true){
        orangeball = true;
        pacXtemp = pacmanX;                                       
        pacYtemp = pacmanY;
        pacDirectionTemp = direction;   
      }
        break;
        
      case KeyEvent.VK_X: 
        if (blueball != true){
        blueball = true;
        pacXtempB = pacmanX;                                       
        pacYtempB = pacmanY;
        pacDirectionTempB = direction;   
      }
        break;
    
    case KeyEvent.VK_ENTER: 
        if (selection == 0)
        startmenu = 1;
        else if (selection == 1){
        startmenu = 2;
        selection = 2;
        } else  if (startmenu == 2){
          if (selection == 2){
          startmenu  = 3;
          selection = 6;
          }
          
        } else if (startmenu == 3){
         
          
        }
        
        break;
    }
  }
  public void keyReleased( KeyEvent e ) {
    
    
    
  }
  public void keyTyped( KeyEvent e ) { 
    
    
    
    
    
  }
  
}

