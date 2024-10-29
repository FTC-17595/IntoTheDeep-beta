package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Slider {
    private final DcMotor sliderMotor;
    private static final int MAX_POSITION = -2200;
    private static final int MIN_POSITION = 50;

    public Slider(HardwareMap hardwareMap) {
        sliderMotor = hardwareMap.dcMotor.get("sliderMotor");
        sliderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void extend(double power) {
        if (sliderMotor.getCurrentPosition() > MAX_POSITION) {
            sliderMotor.setPower(power);
        } else {
            sliderMotor.setPower(0);
        }
    }

    public void contract(double power) {
        if (sliderMotor.getCurrentPosition() < MIN_POSITION) {
            sliderMotor.setPower(power);
        }
    }

    public void sliderTelemetry(Telemetry telemetry) {
        telemetry.addData("Slider", sliderMotor.getCurrentPosition());
    }
}
