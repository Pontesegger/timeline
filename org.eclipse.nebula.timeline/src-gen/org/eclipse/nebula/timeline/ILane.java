/**
 */
package org.eclipse.nebula.timeline;

import java.util.concurrent.TimeUnit;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lane</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.timeline.ILane#getTrack <em>Track</em>}</li>
 *   <li>{@link org.eclipse.nebula.timeline.ILane#getTimeEvents <em>Time Events</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.timeline.ITimelinePackage#getLane()
 * @model
 * @generated
 */
public interface ILane extends EObject {
	/**
	 * Returns the value of the '<em><b>Track</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.timeline.ITrack#getLanes <em>Lanes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Track</em>' container reference.
	 * @see #setTrack(ITrack)
	 * @see org.eclipse.nebula.timeline.ITimelinePackage#getLane_Track()
	 * @see org.eclipse.nebula.timeline.ITrack#getLanes
	 * @model opposite="lanes" required="true" transient="false"
	 * @generated
	 */
	ITrack getTrack();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.timeline.ILane#getTrack <em>Track</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Track</em>' container reference.
	 * @see #getTrack()
	 * @generated
	 */
	void setTrack(ITrack value);

	/**
	 * Returns the value of the '<em><b>Time Events</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.timeline.ITimelineEvent}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.timeline.ITimelineEvent#getLane <em>Lane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time Events</em>' containment reference list.
	 * @see org.eclipse.nebula.timeline.ITimelinePackage#getLane_TimeEvents()
	 * @see org.eclipse.nebula.timeline.ITimelineEvent#getLane
	 * @model opposite="lane" containment="true"
	 * @generated
	 */
	EList<ITimelineEvent> getTimeEvents();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true" timeUnitDataType="org.eclipse.nebula.timeline.TimeUnit"
	 * @generated
	 */
	ITimelineEvent addEvent(String title, String message, long startTimestamp, long endTimestamp, TimeUnit timeUnit);

} // ILane
