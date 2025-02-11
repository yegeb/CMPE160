import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Program for the Angry Bullets game.
 * This class contains the main method to run the game.
 * The game involves shooting bullets at targets while avoiding obstacles.
 *
 * @author Yuksel Ege Boyaci, Student ID: 2023400315
 * @since 08.03.2024
 */

public class YukselEgeBoyaci {

    /**
     * Main method to run the Angry Bullets game.
     *
     * @param args command-line arguments (not used)
     */

    public static void main(String[] args) {

        int canvasWidth = 1600; //screen width
        int canvasHeight = 800; // screen height

        StdDraw.setCanvasSize(canvasWidth,canvasHeight); // creating the canvas
        StdDraw.setXscale(0,canvasWidth); // set the x and y scales
        StdDraw.setYscale(0,canvasHeight);
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 18)); // set the font for the general
        StdDraw.enableDoubleBuffering(); // for faster animations

        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height


        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };
        double[][] targetArray = {
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };




        //Coordinate values for obstacles and targets of my own game scene


        /*
        double[][] obstacleArray = {
                {30,540,40,150},
                {400,0,150,120},
                {400,200,150,250},
                {350,450,30,100},
                {450,650,70,170},
                {650,620,150,180},
                {1000,0,200,360},
                {1350,480,100,150}
        };
        */

        /* Specific angle and velocity values for all target hits:
            (92,225)
            (80,185)
            (23,135)
            (84,285)
            (20,180)
            (80,275)
            (81,334)
            (80,355)
         */
        
        /*
        double[][] targetArray = {
                {20,720,30,30},
                {420,480,40,40},
                {690,0,40,40},
                {650,300,50,50},
                {840,80,40,40},
                {850,700,40,30},
                {1300,50,40,40},
                {1500,520,30,30},
        };
        */

        shootingBullet(obstacleArray, targetArray);
    }

    /**
     * Creates the game environment including obstacles, targets, and the bullet trajectory line.
     *
     * @param obstacleArray array containing obstacle coordinates
     * @param targetArray array containing target coordinates
     * @param bulletVelocity initial velocity of the bullet
     * @param bulletAngle initial angle of the bullet
     */

    public static void creatingGameEnvironment(double[][] obstacleArray, double[][] targetArray,
                                               double bulletVelocity, double bulletAngle) {

        StdDraw.setPenColor(StdDraw.BLACK); // set the to color black for the platform
        double platformX0 = 120.0/2; // center coordinates of the platform
        double platformY0 = 120.0/2;
        double platformHalfLength = 120.0/2; // half-length of the platform
        StdDraw.filledSquare(platformX0,platformY0,platformHalfLength);

        StdDraw.setPenColor(StdDraw.DARK_GRAY); // set the color to dark gray for the obstacles
        for (double[] obstacleCoordinates : obstacleArray) {

            // center coordinates of the obstacles
            double obstacleX0 = obstacleCoordinates[0] + (obstacleCoordinates[2] / 2);
            double obstacleY0 = obstacleCoordinates[1] + (obstacleCoordinates[3] / 2);

            // half-lengths of the obstacles
            double obstacleHalfWidth = (obstacleCoordinates[2] / 2);
            double obstacleHalfHeight = (obstacleCoordinates[3] / 2);
            StdDraw.filledRectangle(obstacleX0,obstacleY0,obstacleHalfWidth,obstacleHalfHeight);

        }

        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // set the color to orange for the targets
        for (double[] targetCoordinates : targetArray) {

            // center coordinates of the targets
            double targetX0 = targetCoordinates[0] + (targetCoordinates[2] / 2);
            double targetY0 = targetCoordinates[1] + (targetCoordinates[3] / 2);

            // half-lengths of the targets
            double targetHalfWidth = (targetCoordinates[2] / 2);
            double targetHalfHeight = (targetCoordinates[3] / 2);
            StdDraw.filledRectangle(targetX0,targetY0,targetHalfWidth,targetHalfHeight);

        }

        // scaling the speed to make it similar to the example which was given
        double bulletVelocityX = bulletVelocity * Math.cos(Math.toRadians(bulletAngle)) / 1.725;
        double bulletVelocityY = bulletVelocity * Math.sin(Math.toRadians(bulletAngle)) / 1.725;

        StdDraw.setPenColor(StdDraw.BLACK); // set the color to black for the shooting line
        StdDraw.setPenRadius(0.01); // set the radius of the pen for the shooting line
        StdDraw.line(120,120,120 + bulletVelocityX, 120 + bulletVelocityY); // draw the shooting line
        StdDraw.setPenColor(StdDraw.WHITE); // set the color to white for the a and v texts
        StdDraw.text(60,80, String.format("a: %.1f", bulletAngle)); // show a dynamic text for a and v
        StdDraw.text(60,50, String.format("v: %.1f", bulletVelocity));

        StdDraw.show();
    }

    /**
     * Handles the shooting of the bullet and controls user input for adjusting bullet velocity and angle.
     *
     * @param obstacleArray array containing obstacle coordinates
     * @param targetArray array containing target coordinates
     */

    public static void shootingBullet(double[][] obstacleArray, double[][] targetArray) {

        double time = 0; // time
        double gravity = 9.80665; // gravity
        double bulletX0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double bulletY0 = 120;
        double bulletRadius = 6; // radius of the bullet
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle

        creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

        int keyboardPauseDuration = 60; // used for the time interval after a key is pressed
        boolean hasBegun = false; // to check if the game has been started

        while (!hasBegun) {

            // bullet will be shot
            if(StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {

                hasBegun = true;
                break;
            }

            // to adjust the bullet velocity and angle before the start

            if(StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {

                StdDraw.pause(keyboardPauseDuration);
                bulletVelocity -= 1;
                creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

            }
            if(StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {

                StdDraw.pause(keyboardPauseDuration);
                bulletVelocity += 1;
                creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

            }
            if(StdDraw.isKeyPressed(KeyEvent.VK_UP)) {

                StdDraw.pause(keyboardPauseDuration);
                bulletAngle += 1;
                creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

            }
            if(StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {

                StdDraw.pause(keyboardPauseDuration);
                bulletAngle -= 1;
                creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

            }

            StdDraw.clear();

        }

        // scaling the speed to make it similar to the example which was given
        double bulletVelocityX = bulletVelocity * Math.cos(Math.toRadians(bulletAngle)) / 1.725;
        double bulletVelocityY = bulletVelocity * Math.sin(Math.toRadians(bulletAngle)) / 1.725;

        double bulletXPrev = bulletX0; // previous bullet coordinates for drawing the line
        double bulletYPrev = bulletY0;

        boolean[] collisions = {false, false, false, false}; // setting initial collision values

        // bullet traveling through the screen in a parabolic path
        while (!collisions[0] && !collisions[1] && !collisions[2] && !collisions[3]) {

            creatingGameEnvironment(obstacleArray , targetArray, bulletVelocity, bulletAngle);

            double bulletX = bulletX0 + bulletVelocityX * time; // calculating the x and y coordinates over time
            double bulletY = bulletY0 + bulletVelocityY * time
                    - (1.0/2 * gravity * Math.pow(time, 2));

            StdDraw.setPenColor(StdDraw.BLACK); // set the color to black for the lines
            StdDraw.setPenRadius(0.003); // set the pen radius for the lines between bullets
            StdDraw.line(bulletXPrev,bulletYPrev,bulletX,bulletY); // drawing line between bullets for visuals

            StdDraw.filledCircle(bulletX, bulletY, bulletRadius); // drawing the bullet
            StdDraw.pause(25); // time used for resetting the screen, determines how fast the bullet is seen.
            time += 0.25; // time used in the calculations, used for the frequency of the drawing
            bulletXPrev = bulletX; // keeping the previous coordinates
            bulletYPrev = bulletY;

            collisions = collisionCheck(bulletX,bulletY,obstacleArray,targetArray); // check collision

        }

        endGame(collisions, obstacleArray, targetArray); // ending game

        // check if the player wants to play it again
        boolean restart = false;
        while(!restart){

            if(StdDraw.isKeyPressed(KeyEvent.VK_R)) {

                restart = true;
                StdDraw.clear();
                shootingBullet(obstacleArray,targetArray); // returning into the game loop

            }

        }
        

    }

    /**
     * Checks for collisions between the bullet and obstacles, targets, or screen borders.
     *
     * @param bulletX x-coordinate of the bullet
     * @param bulletY y-coordinate of the bullet
     * @param obstacleArray array containing obstacle coordinates
     * @param targetArray array containing target coordinates
     * @return array indicating collisions with obstacles, targets, or screen borders
     */

    public static boolean[] collisionCheck(double bulletX, double bulletY,
                                         double[][] obstacleArray, double[][] targetArray) {

        boolean[] collisions = new boolean[4]; // array to keep track of possible collisions

        // it is enough to know only the left bottom and the right top corners for to determine
        // the borders of the boxes to check collision

        // collision check for obstacles
        for (double[] obstacle : obstacleArray) {

            boolean obstacleHit;
            double obstacleLBCornerX = obstacle[0];
            double obstacleLBCornerY = obstacle[1];
            double obstacleRTCornerX = obstacle[0] + obstacle[2];
            double obstacleRTCornerY = obstacle[1] + obstacle[3];

            if (obstacleLBCornerX <= bulletX && obstacleRTCornerX >= bulletX
                && obstacleLBCornerY <= bulletY && obstacleRTCornerY >= bulletY) {

                obstacleHit = true;
                collisions[0] = obstacleHit;

            }
        }

        // collision check for targets
        for (double[] target : targetArray) {

            boolean targetHit;
            double targetLBCornerX = target[0];
            double targetLBCornerY = target[1];
            double targetRTCornerX = target[0] + target[2];
            double targetRTCornerY = target[1] + target[3];

            if (targetLBCornerX <= bulletX && targetRTCornerX >= bulletX
                && targetLBCornerY <= bulletY && targetRTCornerY >= bulletY) {

                targetHit = true;
                collisions[1] = targetHit;

            }
        }

        // collision check for right and left border limits
        boolean screenBorderLimitX;
        if (bulletX <= 0 || bulletX >= 1600) {

                screenBorderLimitX = true;
                collisions[2] = screenBorderLimitX;

        }

        // collision check for bottom limit
        boolean screenBorderLimitY;
        if (bulletY <= 0) {

            screenBorderLimitY = true;
            collisions[3] = screenBorderLimitY;

        }

        return collisions;

    }

    /**
     * Displays end game messages based on collisions and prompts for game restart.
     *
     * @param collisions array indicating collisions with obstacles, targets, or screen borders
     * @param obstacleArray array containing obstacle coordinates
     * @param targetArray array containing target coordinates
     */

    public static void endGame(boolean[] collisions, double[][] obstacleArray, double[][] targetArray) {

        StdDraw.setPenColor(StdDraw.BLACK); // set the color and font for the end game texts
        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 18));

        // end game screen notes
        if(collisions[0]) {

            StdDraw.text(200,750, "Hit an obstacle. Press 'r' to shoot again.");

        }

        if(collisions[1]) {

            StdDraw.text(200,750, "Congratulations: You hit the target!");

        }

        if(collisions[2]) {

            StdDraw.text(200,750, "Max X reached. Press 'r' to shoot again.");

        }

        if(collisions[3]) {

            StdDraw.text(200,750, "You've hit the ground. Press 'r' to shoot again.");

        }

        StdDraw.show();

    }

}
