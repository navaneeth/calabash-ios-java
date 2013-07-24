/**
 * 
 */
package calabash.java;

/**
 * Provides callback while inspecting elements
 * 
 */
public interface InspectCallback {

	void onEachElement(UIElement element, int nestingLevel);

}
