package team.kallisto;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class LimitedScaledMotor implements RegulatedMotor {
	public static final int ALLOWED_DELTA = 3;
	public final RegulatedMotor motor;
	public final int negativeLimit;
	public final int positiveLimit;
	public final int scale;
	public final boolean hasLimit;

	/**
	 * @param motor the motor to control
	 * @param limit the limits for this motor
	 * @param scale the scale to apply after limiting
	 */
	public LimitedScaledMotor(RegulatedMotor motor, int limit, int scale) {
		this(motor, -limit, limit, scale);
	}
	public LimitedScaledMotor(RegulatedMotor motor, int negativeLimit, int positiveLimit, int scale) {
		if (negativeLimit > positiveLimit)
			throw new IllegalArgumentException("negativeLimit must be smaller than positiveLimit");

		this.motor = motor;
		this.negativeLimit = negativeLimit;
		this.positiveLimit = positiveLimit;
		this.scale = scale;
		this.hasLimit = true;
	}

	public LimitedScaledMotor(RegulatedMotor motor) {
		this.hasLimit = false;
		this.motor = motor;
		this.negativeLimit = 0;
		this.positiveLimit = 0;
		this.scale = 1;
	}

	private int clamp(int value) {
		if (hasLimit) {
			return Math.max(Math.min(value, positiveLimit), negativeLimit) * scale;
		}
		return value * scale;
	}

	private int clampRelative(int value) {
		if (hasLimit) {
			return (Math.max(Math.min(value + getTachoCount(), positiveLimit),
					negativeLimit) - getTachoCount()) * scale;
		}
		return value * scale;
	}

	public boolean hasReachedPositiveLimit(int angle) {
		return angle >= positiveLimit - ALLOWED_DELTA;
	}

	public boolean hasReachedNegativeLimit(int angle) {
		return angle <= negativeLimit + ALLOWED_DELTA;
	}

	public void rotateToPositiveLimit(boolean immediateReturn) {
		rotateTo(positiveLimit, immediateReturn);
	}

	public void rotateToNegativeLimit(boolean immediateReturn) {
		rotateTo(negativeLimit, immediateReturn);
	}

	public boolean isMovingForward() {
		return getRotationSpeed() >= 0;
	}

	public void setSpeedRelativeToMax(int percent) {
		float newSpeed = this.getMaxSpeed() * percent / 100.0f;
		this.setSpeed((int) newSpeed);
	}

	@Override
	public void addListener(RegulatedMotorListener listener) {
		motor.addListener(listener);
	}

	@Override
	public RegulatedMotorListener removeListener() {
		return motor.removeListener();
	}

	@Override
	public void stop(boolean immediateReturn) {
		motor.stop(immediateReturn);
	}

	@Override
	public void flt(boolean immediateReturn) {
		motor.flt(immediateReturn);
	}

	@Override
	public void waitComplete() {
		motor.waitComplete();
	}

	@Override
	public void rotate(int angle, boolean immediateReturn) {
		motor.rotate(clampRelative(angle), immediateReturn);
	}

	@Override
	public void rotate(int angle) {
		motor.rotate(clampRelative(angle));
	}

	@Override
	public void rotateTo(int limitAngle) {
		motor.rotateTo(clamp(limitAngle));
	}

	@Override
	public void rotateTo(int limitAngle, boolean immediateReturn) {
		motor.rotateTo(clamp(limitAngle), immediateReturn);
	}

	@Override
	public int getLimitAngle() {
		return motor.getLimitAngle() / scale;
	}

	/**
	 * MIGHT NOT BE EXACT
	 */
	//@Deprecated
	@Override
	public void setSpeed(int speed) {
		motor.setSpeed(speed * scale);
	}

	/**
	 * MIGHT NOT BE EXACT
	 */
	//@Deprecated
	@Override
	public int getSpeed() {
		return motor.getSpeed() / scale;
	}

	/**
	 * MIGHT NOT BE EXACT
	 */
	//@Deprecated
	@Override
	public float getMaxSpeed() {
		return motor.getMaxSpeed() / scale;
	}

	@Override
	public boolean isStalled() {
		return motor.isStalled();
	}

	@Override
	public void setStallThreshold(int error, int time) {
		motor.setStallThreshold(error, time);
	}

	/**
	 * MIGHT NOT BE EXACT
	 */
	@Deprecated
	@Override
	public void setAcceleration(int acceleration) {
		motor.setAcceleration(acceleration);
	}

	@Override
	public void synchronizeWith(RegulatedMotor[] syncList) {
		motor.synchronizeWith(syncList);
	}

	@Override
	public void startSynchronization() {
		motor.startSynchronization();
	}

	@Override
	public void endSynchronization() {
		motor.endSynchronization();
	}

	@Override
	public void close() {
		motor.close();
	}

	@Override
	public void forward() {
		motor.forward();
	}

	@Override
	public void backward() {
		motor.backward();
	}

	@Override
	public void stop() {
		motor.stop();
	}

	@Override
	public void flt() {
		motor.flt();
	}

	@Override
	public boolean isMoving() {
		return motor.isMoving();
	}

	/**
	 * MIGHT NOT BE EXACT
	 */
	//@Deprecated
	@Override
	public int getRotationSpeed() {
		return motor.getRotationSpeed() / scale;
	}

	@Override
	public int getTachoCount() {
		return motor.getTachoCount() / scale;
	}

	@Override
	public void resetTachoCount() {
		motor.resetTachoCount();
	}
}
