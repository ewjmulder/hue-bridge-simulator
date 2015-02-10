package com.programyourhome.huebridgesimulator.upnp;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

@UpnpService(
        serviceId = @UpnpServiceId("InternetGatewayDevice"),
        serviceType = @UpnpServiceType(value = "InternetGatewayDevice", version = 1))
public class SwitchPower {

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private boolean target = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = "NewTargetValue") final boolean newTargetValue) {
        this.target = newTargetValue;
        this.status = newTargetValue;
        System.out.println("Switch is: " + this.status);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget() {
        return this.target;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return this.status;
    }

}
