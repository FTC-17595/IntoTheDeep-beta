/* Copyright (c) 2024 Krish Patel. All rights reserved.
 * This code will reject red samples for the blue alliance.
 * Use this code in autonomous to make sure you aren't trolling the red alliance and the referees.
 * Designed for the FTC-17595 Into the Deep robot.
 * 
 */

package org.firstinspires.ftc.robotcontroller.internal;

import android.app.Activity;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;


@Autonomous(name="Reject Red Samples & Specimen")
@Disabled
public class AutoColorSensorSampleRejectorBlue extends LinearOpMode {

    boolean debugging = false; // Set to true for debugging. Messages wil be displayed in telemetry.

    View relativeLayout; // For the interface

    @Override
    public void runOpMode() throws InterruptedException {
        // Define motors & servos to use
        DcMotor pivotMotor = hardwareMap.dcMotor.get("pivotMotor");
        CRServo pickUpServo = hardwareMap.crservo.get("pickUpServo");
        NormalizedColorSensor colorSensor1 = (NormalizedColorSensor) hardwareMap.colorSensor.get(""); // Insert name of color sensor

        try {
            float gain = 2; // Change later if needed
            colorSensor1.setGain(gain);
        } finally {
            if (debugging) {
                telemetry.addLine("Debugging: Color Sensor");
                telemetry.addData("Color Sensor Error", "GAIN SET FAILED");
                telemetry.update();
            }
        }

        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        waitForStart();

        while(opModeIsActive()) {
            if (colorSensor1.getNormalizedColors().red > 0.667) {
                if (debugging) {
                    telemetry.addLine("Debugging: Color Sensor");
                    telemetry.addData("RED ALERT", "Red sample/specimen detected.");
                    telemetry.update();
                }
                pivotMotor.setTargetPosition(0);
                pickUpServo.setPower(-0.42);
            }
        }
    }
}