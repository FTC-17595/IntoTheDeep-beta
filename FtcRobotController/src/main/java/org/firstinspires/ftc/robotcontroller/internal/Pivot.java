package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Pivot {
    private final
    DcMotor pivotMotor;
    private boolean isPivotUp = false;

    public Pivot(HardwareMap hardwareMap) {
        pivotMotor = hardwareMap.dcMotor.get("pivotMotor");
        pivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void togglePivot() {
        if (isPivotUp) {
            pivotMotor.setTargetPosition(-100);
            pivotMotor.setPower(-0.5);
        } else {
            pivotMotor.setTargetPosition(-2700);
            pivotMotor.setPower(0.5);
        }
        pivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        isPivotUp = !isPivotUp;
    }

    public void pivotTelemetry(Telemetry telemetry) {
        telemetry.addData("Pivot", pivotMotor.getCurrentPosition());
    }
}
