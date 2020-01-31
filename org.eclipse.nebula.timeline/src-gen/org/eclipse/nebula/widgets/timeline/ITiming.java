/**
 */
package org.eclipse.nebula.widgets.timeline;

import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timing</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITiming#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITiming#getDuration <em>Duration</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTiming()
 * @model
 * @generated
 */
public interface ITiming extends EObject {
	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(long)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTiming_Timestamp()
	 * @model
	 * @generated
	 */
	long getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITiming#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(long value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(long)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTiming_Duration()
	 * @model
	 * @generated
	 */
	long getDuration();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITiming#getDuration <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(long value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setStartTimestamp(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setEndTimestamp(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setDuration(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setStartTimestamp(long value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setEndTimestamp(long value);

} // ITiming
