/**
 */
package org.eclipse.nebula.timeline;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timeline</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.timeline.ITimeline#getTracks <em>Tracks</em>}</li>
 *   <li>{@link org.eclipse.nebula.timeline.ITimeline#getCursors <em>Cursors</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.timeline.ITimelinePackage#getTimeline()
 * @model
 * @generated
 */
public interface ITimeline extends EObject {
	/**
	 * Returns the value of the '<em><b>Tracks</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.timeline.ITrack}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.timeline.ITrack#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tracks</em>' containment reference list.
	 * @see org.eclipse.nebula.timeline.ITimelinePackage#getTimeline_Tracks()
	 * @see org.eclipse.nebula.timeline.ITrack#getTimeline
	 * @model opposite="timeline" containment="true"
	 * @generated
	 */
	EList<ITrack> getTracks();

	/**
	 * Returns the value of the '<em><b>Cursors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.timeline.ICursor}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.timeline.ICursor#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cursors</em>' containment reference list.
	 * @see org.eclipse.nebula.timeline.ITimelinePackage#getTimeline_Cursors()
	 * @see org.eclipse.nebula.timeline.ICursor#getTimeline
	 * @model opposite="timeline" containment="true"
	 * @generated
	 */
	EList<ICursor> getCursors();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true"
	 * @generated
	 */
	ITrack createTrack(String title);

} // ITimeline
