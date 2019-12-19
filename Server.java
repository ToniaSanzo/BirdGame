import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket = null;
    private static Socket birdSocket = null;
    private static Socket carSocket = null;
    private static DataInputStream inBird = null;
    private static DataInputStream inCar = null;
    private static DataOutputStream outBird = null;
    private static DataOutputStream outCar = null;
    private static int birdPendingMessage,carPendingMessage;
    private static int birdPoop;
    private static long time = 0;
    private static float cX = 0,bX = 240,bY = 0;


    private static final float END_FLOAT = 7432;

    public static void main(String [] args){

        // generate the 2 sockets
        try {
            serverSocket = new ServerSocket(2720);
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        }

        try {
            birdSocket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        }

        System.out.println("BIRD CONNECTED");

        try {
            serverSocket = new ServerSocket(2721);
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        }

        try {
            carSocket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        }


        System.out.println("CAR CONNECTED");

        try {
            inBird = new DataInputStream(birdSocket.getInputStream());
            inCar = new DataInputStream(carSocket.getInputStream());
            outBird = new DataOutputStream(birdSocket.getOutputStream());
            outCar = new DataOutputStream(carSocket.getOutputStream());
        } catch (IOException e) {
            return;
        }

        try {
            outBird.writeInt(1);
            outCar.writeInt(1);
        } catch(IOException e){
            e.printStackTrace();
        }



        time = System.nanoTime();
        //while (true) {
        while (600000000000L > System.nanoTime() - time) {

            // Check to see if any messages are pending
            try {
                birdPendingMessage = inBird.available();
            }catch (IOException e) {
                e.printStackTrace();
            }

            // Check to see if any messages are pending
            try {
                carPendingMessage = inCar.available();
            }catch (IOException e) {
                e.printStackTrace();
            }


            // If there is a pending message write the birds location to the car.
            if(birdPendingMessage != 0){
                try{
                    bX = inBird.readFloat();
                    bY = inBird.readFloat();
                    birdPoop = inBird.readInt();
                    // UPDATES CARS BIRD STATE
                    System.out.println("bX = " + bX + "bY = " + bY + "birdPoop = " + birdPoop + ":: cX written to the bird socket = " + cX);
                    //outBird.writeFloat(cX);
                    outCar.writeFloat(bX);
                    outCar.writeFloat(bY);
                    outCar.writeInt(birdPoop);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }


            // If there is a pending message write the cars  location to the bird.
            if(carPendingMessage != 0){
                try{
                    cX = inCar.readFloat();
                    // UPDATES BIRDS CAR STATE

                    System.out.println("cX = " + cX + ":: written to the car socket bX = "+ bX + " bY = " + bY);
                    //outCar.writeFloat(bX);
                    //outCar.writeFloat(bY);
                    outBird.writeFloat(cX);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }


        // write the values so that the server knows to shut down
/*        try {
            //outBird.writeFloat(END_FLOAT);
            //outCar.writeFloat(END_FLOAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Close the server sockets
        try {
            birdSocket.close();
            carSocket.close();
            serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
