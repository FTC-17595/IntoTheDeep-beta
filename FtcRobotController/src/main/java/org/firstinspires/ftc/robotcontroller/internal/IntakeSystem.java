package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class IntakeSystem {
    private final CRServo pickUpServo;
    private final Servo turnServo;

    public IntakeSystem(HardwareMap hardwareMap) {
        pickUpServo = hardwareMap.crservo.get("intakeServo");
        turnServo = hardwareMap.servo.get("turnServo");
    }

    public void pickUp() {
        pickUpServo.setPower(0.5);
    }

    public void dropOff() {
        pickUpServo.setPower(-0.5);
    }

    public void stopIntake() {
        pickUpServo.setPower(0);
    }

    public void rotateLeft() {
        turnServo.setPosition(0.0);
    }

    public void rotateRight() {
        turnServo.setPosition(1.0);
    }

    public void centerRotation() {
        turnServo.setPosition(0.85);
    }

    public void servoTelemetry(Telemetry telemetry) {
        telemetry.addData("Rotate Intake", turnServo.getPosition());
        telemetry.addData("Intake Power", pickUpServo.getPower());
    }
}
