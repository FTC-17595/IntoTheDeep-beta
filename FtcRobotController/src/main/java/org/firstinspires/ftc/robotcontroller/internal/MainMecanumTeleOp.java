package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Main Mecanum Drive")
public class MainMecanumTeleOp extends LinearOpMode {

    static int SLIDER_MAX_POSITION = -2200;
    static int SLIDER_MIN_POSITION = 50;

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor, pivotMotor, sliderMotor;
    private CRServo pickUpServo;
    private Servo turnServo;

    private int counter = 1;
    private boolean ifPivotUp = false;
    private boolean intakeReleased = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initializeHardware();
        waitForStart();
        if (isStopRequested()) return;
        mainTeleOpLoop();
    }

    private void initializeHardware() {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        pivotMotor = hardwareMap.dcMotor.get("pivotMotor");
        sliderMotor = hardwareMap.dcMotor.get("sliderMotor");
        pickUpServo = hardwareMap.crservo.get("intakeServo");
        turnServo = hardwareMap.servo.get("turnServo");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        resetEncoders();
        configureMotorModes();
        servoDropOffPosition(turnServo);
    }

    private void resetEncoders() {
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sliderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void configureMotorModes() {
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pivotMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sliderMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void mainTeleOpLoop() {
        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = -gamepad1.left_stick_x * 1.1;
            double rx = -gamepad1.right_stick_x;
            boolean pivotMove = gamepad1.right_bumper;
            double sliderExtend = -gamepad1.right_trigger;
            double sliderContract = gamepad1.left_trigger;
            boolean dropIt = gamepad1.a;
            boolean pickIt = gamepad1.b;
            boolean intakeStop = gamepad1.x;
            boolean resetIntake = gamepad1.y;
            boolean turnLeft = gamepad1.dpad_left;
            boolean turnRight = gamepad1.dpad_right;
            boolean turnCenter = gamepad1.dpad_down;

            double frontLeftPower = ((y + x + rx) / getDenominator(y, x, rx)) / 1.5;
            double frontRightPower = ((y - x - rx) / getDenominator(y, x, rx)) / 1.5;
            double backLeftPower = ((y - x + rx) / getDenominator(y, x, rx)) / 1.5;
            double backRightPower = ((y + x - rx) / getDenominator(y, x, rx)) / 1.5;

            if (counter == 1) {
                servoDropOffPosition(turnServo);
            }

            contractingSlider(sliderContract, sliderMotor);
            extendingSlider(sliderExtend, sliderMotor);

            controlIntake(pickIt, dropIt, intakeStop);
            rotateIntake(turnLeft, turnRight, turnCenter);
            controlPivot(pivotMove);
            resetIntakePosition(resetIntake);

            setMotorPowers(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
            displayTelemetry();
            counter += 1;
        }
    }

    private double getDenominator(double y, double x, double rx) {
        return Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
    }

    private void servoDropOffPosition(Servo turnServo) {
        turnServo.setPosition(0);
    }

    private void contractingSlider(double sliderContract, DcMotor sliderMotor) {
        if (sliderContract > 0) {
            double position = sliderMotor.getCurrentPosition();
            if (position < SLIDER_MIN_POSITION) {
                sliderMotor.setPower(sliderContract);
            }
            telemetry.addData("sliderPosition: ", sliderMotor.getCurrentPosition());
        }
    }

    private void extendingSlider(double sliderExtend, DcMotor sliderMotor) {

        if (sliderExtend < 0) {
            double position = sliderMotor.getCurrentPosition();
            if (position > SLIDER_MAX_POSITION) {
                sliderMotor.setPower(sliderExtend);
            } else {
                sliderMotor.setPower(0);
            }
            telemetry.addData("sliderPosition: ", sliderMotor.getCurrentPosition());
        }
    }

    private void controlIntake(boolean pickIt, boolean dropIt, boolean intakeStop) {
        if (pickIt) {
            pickUpServo.setPower(0.5);
        } else if (dropIt) {
            pickUpServo.setPower(-0.5);
        } else if (intakeStop) {
            pickUpServo.setPower(0);
        }
    }

    private void rotateIntake(boolean turnLeft, boolean turnRight, boolean turnCenter) {
        if (turnLeft) {
            turnServo.setPosition(0.0);
        } else if (turnRight) {
            turnServo.setPosition(1.0);
        } else if (turnCenter) {
            turnServo.setPosition(0.85);
        }
    }

    private void controlPivot(boolean pivotMove) {
        if (pivotMove) {
            if (ifPivotUp) {
                pivotMotor.setTargetPosition(-100);
                pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                pivotMotor.setPower(-0.5);
                ifPivotUp = false;
            } else {
                pivotMotor.setTargetPosition(-2700);
                pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                pivotMotor.setPower(0.5);
                ifPivotUp = true;
            }
        }
    }

    private void resetIntakePosition(boolean resetIntake) {
        if (resetIntake) {
            if (!intakeReleased) {
                pivotMotor.setTargetPosition(-275);
                pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                pivotMotor.setPower(-0.4);
                turnServo.setPosition(0.85);
                sliderMotor.setTargetPosition(-500);
                sliderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderMotor.setPower(0.3);
                intakeReleased = true;
            } else {
                pivotMotor.setTargetPosition(-2600);
                pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                pivotMotor.setPower(0.4);
                turnServo.setPosition(0.0);
                sliderMotor.setTargetPosition(-200);
                sliderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sliderMotor.setPower(0.4);
                sliderMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                intakeReleased = false;
            }
        }
    }

    private void setMotorPowers(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }

    private void displayTelemetry() {
        telemetry.addLine("Centralized Telemetry:");
        telemetry.addData("Stick: ", " %5.2f, %5.2f, %5.2f", gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        telemetry.addData("Right Trigger", -gamepad1.right_trigger);
        telemetry.addData("Left Trigger", gamepad1.left_trigger);
        telemetry.addLine("Motor Encoders:");
        telemetry.addData("Front Left", frontLeftMotor.getCurrentPosition());
        telemetry.addData("Front Right", frontRightMotor.getCurrentPosition());
        telemetry.addData("Back Left", backLeftMotor.getCurrentPosition());
        telemetry.addData("Back Right", backRightMotor.getCurrentPosition());
        telemetry.addData("Pivot", pivotMotor.getCurrentPosition());
        telemetry.addData("Slider", sliderMotor.getCurrentPosition());
        telemetry.addLine("Servo Positions:");
        telemetry.addData("Rotate Intake", turnServo.getPosition());
        telemetry.addData("Intake Power", pickUpServo.getPower());
        telemetry.update();
    }
}