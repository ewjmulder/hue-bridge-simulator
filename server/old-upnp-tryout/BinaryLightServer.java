package com.programyourhome.huebridgesimulator.upnp;

import java.io.IOException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

public class BinaryLightServer implements Runnable {

    public static void main(final String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        final Thread serverThread = new Thread(new BinaryLightServer());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    @Override
    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    this.createDevice()
                    );

        } catch (final Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        final DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier("Demo Binary Light"));
        final DeviceType type = new UDADeviceType("InternetGatewayDevice", 1);
        final DeviceDetails details =
                new DeviceDetails(
                        "Friendly Binary Light",
                        new ManufacturerDetails("ACME"),
                        new ModelDetails(
                                "BinLight2000",
                                "A demo light with on/off switch.",
                                "v1"
                        )
                );
        // final Icon icon = new Icon("image/png", 48, 48, 8, new File("Filename-does-not-exist"));

        final LocalService<SwitchPower> switchPowerService = new AnnotationLocalServiceBinder().read(SwitchPower.class);

        switchPowerService.setManager(new DefaultServiceManager(switchPowerService, SwitchPower.class));

        return new LocalDevice(identity, type, details, switchPowerService);

        /*
         * Several services can be bound to the same device:
         * return new LocalDevice(
         * identity, type, details, icon,
         * new LocalService[] {switchPowerService, myOtherService}
         * );
         */
    }

}
