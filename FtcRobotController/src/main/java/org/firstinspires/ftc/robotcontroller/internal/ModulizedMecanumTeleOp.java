package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Modularized Main Mecanum Drive")
public class ModulizedMecanumTeleOp extends LinearOpMode {
    private DriveTrain driveTrain;
    private Slider slider;
    private Pivot pivot;
    private IntakeSystem intakeSystem;

    @Override
    public void runOpMode() throws InterruptedException {
        driveTrain = new DriveTrain(hardwareMap);
        slider = new Slider(hardwareMap);
        pivot = new Pivot(hardwareMap);
        intakeSystem = new IntakeSystem(hardwareMap);

        driveTrain.resetEncoders();
        driveTrain.configureMotorModes();
        intakeSystem.centerRotation();

        waitForStart();

        while (opModeIsActive()) {
            drive();
            manageSlider();
            intakeControl();
            pivotControl();
            displayTelemetry();
        }
    }

    private void drive() {
        double y = gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x * 1.1;
        double rx = -gamepad1.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        driveTrain.setMotorPowers((y + x + rx) / denominator, (y - x - rx) / denominator,
            (y - x + rx) / denominator, (y + x - rx) / denominator);
    }

    private void manageSlider() {
        slider.contract(gamepad1.left_trigger);
        slider.extend(-gamepad1.right_trigger);
    }

    private void intakeControl() {
        if (gamepad1.a) intakeSystem.dropOff();
        if (gamepad1.b) intakeSystem.pickUp();
        if (gamepad1.x) intakeSystem.stopIntake();
        if (gamepad1.dpad_left) intakeSystem.rotateLeft();
        if (gamepad1.dpad_right) intakeSystem.rotateRight();
        if (gamepad1.dpad_down) intakeSystem.centerRotation();
    }

    private void pivotControl() {
        if (gamepad1.right_bumper) pivot.togglePivot();
    }

    private void displayTelemetry() {
        telemetry.addLine("Centralized Telemetry:");
        telemetry.addData("Stick: ", " %5.2f, %5.2f, %5.2f", gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        telemetry.addData("Right Trigger", -gamepad1.right_trigger);
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addLine("Motor Encoders:");
        driveTrain.driveTrainTelemetry(telemetry);
        pivot.pivotTelemetry(telemetry);
        slider.sliderTelemetry(telemetry);
        telemetry.addLine("Servo Positions:");
        intakeSystem.servoTelemetry(telemetry);
        telemetry.update();
    }
}
