package me.thatshawt.gameClient;
import org.hid4java.*;

public class GameControllerThread extends Thread {

    private static final int POLLING_SLEEP_MS = 30;
    private static final int TILES_PER_SECOND = 7;

    private GameClient gameClient;
    private HidDevice hidDevice;

    public GameControllerThread(GameClient gameClient){
        this.gameClient = gameClient;
    }

    public void init(){
        // Configure to use custom specification
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();

        // Use the v0.7.0 manual start feature to get immediate attach events
        hidServicesSpecification.setAutoStart(false);

        // Get HID services using custom specification
        HidServices hidServices = HidManager.getHidServices(hidServicesSpecification);

        // Manually start the services to get attachment event
        hidServices.start();

        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
//            System.out.printf("vendor id: %s usage: %s, product: %s\n"
//                    , hidDevice.getVendorId(), hidDevice.getUsage(), hidDevice.getProduct());
            if(hidDevice.getUsage() == 5 && hidDevice.getVendorId() == 0x54c){
                System.out.println(hidDevice);
                hidDevice.open();

                this.hidDevice = hidDevice;

                hidDevice.setNonBlocking(false);
                this.start();
            }
        }
    }

    @Override
    public void run() {
        float counter = 0;
        while(true) {
            try {
                Byte[] buffer = hidDevice.read();

                // :pray::pray::pray: https://www.psdevwiki.com/ps4/DS4-USB
                if(counter/1000.0 >= 1.0/(float)TILES_PER_SECOND) {
                    counter = 0;
                    switch (buffer[5] & 0b1111) {
                        case 0:
//                        System.out.print("↑, ");
                            gameClient.player.moveUp();
                            break;
                        case 1:
//                        System.out.print("↑→, ");
                            gameClient.player.moveUp();
                            gameClient.player.moveRight();
                            break;
                        case 2:
//                        System.out.print("→, ");
                            gameClient.player.moveRight();
                            break;
                        case 3:
//                        System.out.print("↓→, ");
                            gameClient.player.moveDown();
                            gameClient.player.moveRight();
                            break;
                        case 4:
//                        System.out.print("↓, ");
                            gameClient.player.moveDown();
                            break;
                        case 5:
//                        System.out.print("↓←, ");
                            gameClient.player.moveDown();
                            gameClient.player.moveLeft();
                            break;
                        case 6:
//                        System.out.print("←, ");
                            gameClient.player.moveLeft();
                            break;
                        case 7:
//                        System.out.print("↑←, ");
                            gameClient.player.moveUp();
                            gameClient.player.moveLeft();
                            break;
                        case 8:
//                        System.out.print("RELEASE, ");
                            break;
                    }
                }

//                System.out.printf("left xaxis: %d, left yaxis: %d\n"
//                        , buffer[1], buffer[2]);
//                        System.out.print("{ ");
//                        for (byte b : buffer) {
//                            System.out.printf("%X, ", b);
//                        }
//                        System.out.print(" }\n\n\n");
                try {
                    Thread.sleep(POLLING_SLEEP_MS);
                    counter += POLLING_SLEEP_MS;

                } catch (InterruptedException ignore) {

                }
            } catch (Exception poop) {
                poop.printStackTrace();
            }
        }
    }
}
