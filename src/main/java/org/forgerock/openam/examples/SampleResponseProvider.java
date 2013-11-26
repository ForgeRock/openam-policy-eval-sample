/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: SampleResponseProvider.java,v 1.2 2008/09/05 01:45:02 dillidorai Exp $
 *
 * Portions Copyright 2013 ForgeRock AS
 *
 */

package org.forgerock.openam.examples;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.Syntax;
import com.sun.identity.policy.interfaces.ResponseProvider;

import java.util.*;

/**
 * This class is an implementation of <code>ResponseProvider</code> interface. 
 * It takes as input the attribute for which values are to be fetched from
 * the access manager and sent back in the Policy Decision.
 * <p>
 * If the attribute does not exist in the use profile no value is sent
 * back in the response.
 * <p>
 * It relies on underlying identity repository service
 * to fetch the attribute values for the Subject(s) defined in the policy.
 * <p>
 * It computes a <code>Map</code> of response attributes
 * based on the <code>SSOToken</code>, resource name and env map passed
 * in the method call <code>getResponseDecision()</code>.
 *
 * The policy framework would make a call to the ResponseProvider in a
 * policy only if the policy is applicable to a request as determined by 
 * <code>SSOToken</code>, resource name, <code>Subjects</code>
 * and <code>Conditions</code>.
 */
public class SampleResponseProvider implements ResponseProvider {

    public static final String ATTRIBUTE_NAME = "AttributeName";

    private Map properties;
    private static List propertyNames = new ArrayList(1);

    private boolean initialized = false;

    static {
        propertyNames.add(ATTRIBUTE_NAME);
    }

    /**
     * No argument constructor.
     */
    public SampleResponseProvider () {
    }

    /**
     * Initialize the SampleResponseProvider object by using the configuration
     * information passed by the Policy Framework.
     *
     * @param configParams the configuration information
     * @throws PolicyException if an error occurred during
     *                         initialization of the instance
     */
    public void initialize(Map configParams) throws PolicyException {
        // Get the organization name.
        Set orgNameSet = (Set) configParams.get(
                PolicyManager.ORGANIZATION_NAME);
        if ((orgNameSet != null) && (orgNameSet.size() != 0)) {
            Iterator items = orgNameSet.iterator();
            String orgName = (String) items.next();
        }
        /**
         * Organization name is not used in this sample, but this is code
         * to illustrate how any other custom response provider can get data
         * out from the policy configuration service and use it in 
         * getResponseDecision() as necessary.
         */
        initialized = true;
    }

    /**
     * Returns a list of property names for the response provider.
     *
     * @return <code>List</code> of property names
     */
    public List getPropertyNames()  {
         return propertyNames;
    }

    /**
     * Returns the syntax for a property name.
     * @see com.sun.identity.policy.Syntax
     *
     * @param property property name
     *
     * @return <code>Syntax<code> for the property name
     */
    public Syntax getPropertySyntax(String property) {
        return (Syntax.LIST);
    }

    /**
     * Gets the display name for the property name.
     * The <code>locale</code> variable could be used by the plugin to
     * customize the display name for the given locale.
     * The <code>locale</code> variable could be <code>null</code>, in which
     * case the plugin must use the default locale.
     *
     * @param property property name
     * @param locale   locale for which the property name must be customized
     * @return display name for the property name
     * @throws PolicyException if unable to get the display name
     */
    public String getDisplayName(String property, Locale locale)
            throws PolicyException {
        return property;
    }

    /**
     * Returns a set of valid values given the property name.
     *
     * @param property property name from the PolicyConfig Service
     *                 configured for the specified realm.
     * @return Set of valid values for the property.
     * @throws PolicyException if unable to get the Syntax.
     */
    public Set getValidValues(String property) throws PolicyException {
        if (!initialized) {
            throw (new PolicyException("IdRepo response provider not yet "
                    + "initialized"));
        }
        return Collections.EMPTY_SET;
    }

    /**
     * Sets the properties of the response provider plugin.
     * This influences the response attribute-value Map that would be
     * computed by a call to method <code>getResponseDecision(Map)</code>
     * These attribute-value pairs are encapsulated in
     * <code>ResponseAttribute</code> element tag which is a child of the
     * <code>PolicyDecision</code> element in the PolicyResponse xml
     * if the policy is applicable to the user for the resource, subject and
     * conditions defined.
     *
     * @param properties the properties of the responseProvider
     *                   Keys of the properties have to be String.
     *                   Value corresponding to each key have to be a Set of
     *                   String elements. Each implementation of
     *                   ResponseProvider could add further restrictions
     *                   on the keys and values of this map.
     * @throws PolicyException for any abnormal condition
     */
    public void setProperties(Map properties) throws PolicyException {
        if ((properties == null) || (properties.isEmpty())) {
            throw new PolicyException("Properties cannot be null or empty");
        }
        this.properties = properties;

        //Check if the keys needed for this provider are present namely
        // ATTRIBUTE_NAME
        if (!properties.containsKey(ATTRIBUTE_NAME)) {
            throw new PolicyException("Missing required property");
        }

        // Additional validation on property name and values could be done.
    }

    /**
     * Get the properties of the response provider.
     *
     * @return properties of the response provider.
     */
    public Map getProperties() {
        return (properties == null)
                ? null : Collections.unmodifiableMap(properties);
    }

    /**
     * Gets the response attributes computed by this ResponseProvider object,
     * based on the SSOToken and map of environment parameters.
     *
     * @param token single-sign-on token of the user
     * @param env   specific environment map of key/value pairs
     * @return a Map of response attributes.
     *         Keys of the Map are attribute names ATTRIBUTE_NAME or
     *         Value is a Set of Strings representing response attribute
     *         values.
     * @throws PolicyException if the decision could not be computed
     * @throws SSOException    if SSO token is not valid
     */
    public Map getResponseDecision(SSOToken token, Map env)
            throws PolicyException, SSOException {

        Map respMap = new HashMap();
        Set attrs = (Set) properties.get(ATTRIBUTE_NAME);
        if ((attrs != null) && !(attrs.isEmpty())) {
            try {
                if (token.getPrincipal() != null) {
                    AMIdentity id = IdUtils.getIdentity(token);
                    Map idRepoMap = id.getAttributes(attrs);
                    if (idRepoMap != null) {
                        for (Object attr : attrs) {
                            String attrName = (String) attr;
                            Set values = new HashSet();
                            Set subValues = (Set) idRepoMap.get(attrName);
                            if (subValues != null) {
                                values.addAll(subValues);
                            }
                            respMap.put(attrName, values);
                        }
                    }
                } else {
                    throw (new PolicyException("SSOToken principal is null"));
                }
            } catch (IdRepoException ide) {
                throw new PolicyException(ide);
            }
        }
        return respMap;
    }

    /**
     * Returns a copy of this object.
     *
     * @return a copy of this object
     */
    public Object clone() {
        SampleResponseProvider theClone = null;
        try {
            theClone = (SampleResponseProvider)super.clone();
        } catch (CloneNotSupportedException e) {
            // this should never happen
            throw new InternalError();
        }

        if (properties != null) {
            theClone.properties = new HashMap();
            for (Object obj : properties.keySet()) {
                Set values = new HashSet();
                values.addAll((Set) properties.get(obj));
                theClone.properties.put(obj, values);
            }
        }
        return theClone;
    }
}
