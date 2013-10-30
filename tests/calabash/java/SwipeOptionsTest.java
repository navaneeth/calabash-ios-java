package calabash.java;

import static org.junit.Assert.*;

import org.junit.Test;

import calabash.java.SwipeOptions.Force;
import calabash.java.SwipeOptions.SwipeDelta;

public class SwipeOptionsTest {

	@Test
	public void swipeOptionsWithForce() {
		SwipeOptions swipeOptions = new SwipeOptions(Force.Strong, null);
		assertEquals("{:force => :strong}", swipeOptions.toString());
	}

	@Test
	public void swipeOptionsWithSwipeDeltaHorizontal() {
		SwipeOptions swipeOptions = new SwipeOptions(null, new SwipeDelta(
				new Offset(10, 20), null));
		assertEquals(
				"{'swipe-delta' => {:horizontal => {:dx => 10, :dy => 20}}}",
				swipeOptions.toString());
	}

	@Test
	public void swipeOptionsWithSwipeDeltaVertical() {
		SwipeOptions swipeOptions = new SwipeOptions(null, new SwipeDelta(null,
				new Offset(10, 20)));
		assertEquals(
				"{'swipe-delta' => {:vertical => {:dx => 10, :dy => 20}}}",
				swipeOptions.toString());
	}

	@Test
	public void swipeOptionsWithSwipeDeltaHorizontalAndVertical() {
		SwipeOptions swipeOptions = new SwipeOptions(null, new SwipeDelta(
				new Offset(10, 20), new Offset(10, 20)));
		assertEquals(
				"{'swipe-delta' => {:horizontal => {:dx => 10, :dy => 20}, :vertical => {:dx => 10, :dy => 20}}}",
				swipeOptions.toString());
	}

	@Test
	public void swipeOptionsWithForceAndSwipeDeltaHorizontalAndVertical() {
		SwipeOptions swipeOptions = new SwipeOptions(Force.Normal,
				new SwipeDelta(new Offset(10, 20), new Offset(10, 20)));
		assertEquals(
				"{:force => :normal, 'swipe-delta' => {:horizontal => {:dx => 10, :dy => 20}, :vertical => {:dx => 10, :dy => 20}}}",
				swipeOptions.toString());
	}

}
