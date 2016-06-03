package org.alfresco.repo.security.authentication.ldap;

import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
/**
 * A BeanInfo providing metadata for the Monitor class
 * 
 * @author mrogers
 */
public class MonitorBeanInfo extends SimpleBeanInfo
{
    /**
     * Gets the beans <code>BeanDescriptor</code>.
     *
     * @return  A BeanDescriptor providing overall information about
     * the bean, such as its displayName, its customizer, etc.  May
     * return null if the information should be obtained by automatic
     * analysis.
     */
//    BeanDescriptor getBeanDescriptor();

    /**
     * Gets the beans <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
//    EventSetDescriptor[] getEventSetDescriptors();

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by humans when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *          returned by getEventSetDescriptors.
     * <P>      Returns -1 if there is no default event.
     */
//    int getDefaultEventIndex();

    /**
     * Returns descriptors for all properties of the bean.
     * May return {@code null} if the information
     * should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array
     * will belong to the {@link IndexedPropertyDescriptor} subclass
     * of the {@link PropertyDescriptor} class.
     * A client of the {@code getPropertyDescriptors} method
     * can use "{@code instanceof}" to check
     * whether a given {@code PropertyDescriptor}
     * is an {@code IndexedPropertyDescriptor}.
     *
     * @return an array of {@code PropertyDescriptor}s
     *         describing all properties supported by the bean
     *         or {@code null}
     */
//    PropertyDescriptor[] getPropertyDescriptors();

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     *          returned by getPropertyDescriptors.
     * <P>      Returns -1 if there is no default property.
     */
//    int getDefaultPropertyIndex();

    /**
     * Gets the beans <code>MethodDescriptor</code>s.
     *
     * @return An array of MethodDescriptors describing the externally
     * visible methods supported by this bean.  May return null if
     * the information should be obtained by automatic analysis.
     */
//    MethodDescriptor[] getMethodDescriptors();

    /**
     * This method allows a BeanInfo object to return an arbitrary collection
     * of other BeanInfo objects that provide additional information on the
     * current bean.
     * <P>
     * If there are conflicts or overlaps between the information provided
     * by different BeanInfo objects, then the current BeanInfo takes precedence
     * over the getAdditionalBeanInfo objects, and later elements in the array
     * take precedence over earlier ones.
     *
     * @return an array of BeanInfo objects.  May return null.
     */
//    BeanInfo[] getAdditionalBeanInfo();

}
