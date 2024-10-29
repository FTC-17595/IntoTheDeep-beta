package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "LimelightAprilTagAutonomous", group = "Autonomous")
public class LimelightAprilTagAutonomous extends LinearOpMode {

    private static final String LIMELIGHT_URL = "http://limelight.local:5807/data.json";
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize motors
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRight = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeft = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRight = hardwareMap.get(DcMotor.class, "backRightMotor");

        // Set motor directions if necessary
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            try {
                JSONObject limelightData = getLimelightData();

                // Extract data
                boolean hasTarget = limelightData.getJSONObject("Results").getBoolean("HasTarget");
                double tx = limelightData.getJSONObject("Results").getDouble("tx");
                double ty = limelightData.getJSONObject("Results").getDouble("ty");
                double ta = limelightData.getJSONObject("Results").getDouble("ta");
                int tagID = limelightData.getJSONObject("Results").getInt("tid");

                telemetry.addData("Has Target", hasTarget);
                telemetry.addData("tx", tx);
                telemetry.addData("ty", ty);
                telemetry.addData("ta", ta);
                telemetry.addData("Tag ID", tagID);
                telemetry.update();

                // Implement movement logic based on the data
                if (hasTarget) {
                    // Move towards the sample
                    moveToSample(tx, ty);
                } else {
                    // Search for the sample
                    searchForSample();
                }
            } catch (Exception e) {
                telemetry.addData("Error", e.getMessage());
                telemetry.update();
            }
        }

        // Stop all motors
        stopMoving();
    }

    private JSONObject getLimelightData() throws Exception {
        URL url = new URL(LIMELIGHT_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(100); // Adjust as necessary
        conn.setReadTimeout(100);    // Adjust as necessary

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder jsonData = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            jsonData.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return new JSONObject(jsonData.toString());
    }

    private void moveToSample(double tx, double ty) {
        // Define acceptable thresholds
        double txThreshold = 1.0; // degrees
        double tyThreshold = 1.0; // degrees

        // PID coefficients (you need to tune these)
        double kP = 0.02;

        // Calculate control values
        double steeringAdjust = tx * kP;

        // Adjust heading based on tx (horizontal offset)
        if (Math.abs(tx) > txThreshold) {
            if (tx > 0) {
                // Target is to the right; turn right
                turnRight(steeringAdjust);
            } else {
                // Target is to the left; turn left
                turnLeft(-steeringAdjust);
            }
        } else {
            // Target is centered horizontally; stop turning
            stopMoving();
        }

        // Move forward if ty indicates the target is not centered vertically
        if (Math.abs(ty) > tyThreshold) {
            driveForward(0.3); // Adjust speed as necessary
        } else {
            stopMoving();
            // Target is centered; proceed to pick up the sample
            pickUpSample();
            // Navigate to the basket after picking up the sample
            navigateToBasket();
        }
    }

    private void searchForSample() {
        // Rotate the robot to search for the sample
        turnLeft(0.2); // Rotate left at a slow speed
    }

    private void pickUpSample() {
        // Implement your sample pickup logic here
        telemetry.addLine("Picking up sample...");
        telemetry.update();
        // Simulate time taken to pick up the sample
        sleep(1000);
    }

    private void navigateToBasket() {
        // Implement navigation logic to the basket
        telemetry.addLine("Navigating to basket...");
        telemetry.update();
        // For simplicity, move forward for a set duration
        driveForward(0.5); // Move forward at half speed
        sleep(2000); // Move forward for 2 seconds
        stopMoving();

        // Drop the sample
        dropSample();
    }

    private void dropSample() {
        // Implement your sample drop logic here
        telemetry.addLine("Dropping sample...");
        telemetry.update();
        // Simulate time taken to drop the sample
        sleep(1000);
    }

    // Movement control methods
    private void driveForward(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    private void driveBackward(double power) {
        driveForward(-power);
    }

    private void strafeLeft(double power) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(-power);
    }

    private void strafeRight(double power) {
        strafeLeft(-power);
    }

    private void turnLeft(double power) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(-power);
        backRight.setPower(power);
    }

    private void turnRight(double power) {
        turnLeft(-power);
    }

    private void stopMoving() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}