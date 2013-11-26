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
 * $Id: SampleSubject.java,v 1.2 2008/09/05 01:45:01 dillidorai Exp $
 *
 * Portions Copyright 2013 ForgeRock AS
 *
 */

package org.forgerock.openam.examples;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.policy.*;
import com.sun.identity.policy.interfaces.Subject;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

//import java.security.Principal;

/**
 * The class <code>Subject</code> defines a collection
 * of users (or subjects) to whom the specified policy is applied.
 * A complete implementation of this interface can have complex
 * boolean operations to determine if the given user identified
 * by the <code>SSOToken</code> belongs to this collection.
 * <p>
 * The interfaces are separated into administrative
 * interfaces and evaluation interfaces. The administrative interfaces
 * are used by a GUI/CLI component to create a <code>Subject</code>
 * object and the evaluation interfaces is used by the policy evaluator.
 * <p>
 * This sample implementation defines the collection of all users who have
 * been authenticated (users with valid SSOTokens).
 */
public class SampleSubject implements Subject {

    /**
     * Constructor with no parameter.
     */
    public SampleSubject() {
        // do nothing
    }

    /**
     * Initialize (or configure) the <code>Subject</code> object.
     * Usually it is initialized with the environment
     * parameters set by the system administrator via SMS.
     * For example in a Role implementation, the configuration
     * parameters could specify the directory server name, port, etc.
     *
     * @param configParams configuration parameters as a map.
     * The values in the map is <code>java.util.Set</code>,
     * which contains one or more configuration parameters.
     *
     * @throws PolicyException if an error occurred during
     * initialization of <code>Subject</code> instance
     */
    public void initialize(Map configParams) throws PolicyException {
        // do nothing
    }

    /**
     * Returns the syntax of the values that the
     * <code>Subject</code> implementation can have.
     * @see com.sun.identity.policy.Syntax
     *
     * @param token the <code>SSOToken</code> that is used
     * to determine the syntax
     *
     * @throws SSOException if SSO token is not valid
     * @throws PolicyException if unable to get the list of valid names
     *
     * @return syntax of the values for the <code>Subject</code>
     */
    public Syntax getValueSyntax(SSOToken token)
            throws SSOException, PolicyException {
        return (Syntax.CONSTANT);
    }

    /**
     * Returns the valid values the <code>Subject</code>
     * implementation can have.
     * @see com.sun.identity.policy.ValidValues
     *
     * @param token the <code>SSOToken</code> that is used
     * to determine the valid values
     *
     * @throws SSOException if SSO token is not valid
     * @throws PolicyException if unable to get the list of valid values.
     *
     * @return set of valid values
     */
    public ValidValues getValidValues(SSOToken token)
            throws SSOException, PolicyException {
        return (new ValidValues(ValidValues.SUCCESS, Collections.EMPTY_SET));
    }

    /**
     * Returns a list of possible values for the <code>Subject</code>.
     * The implementation must use the <code>SSOToken</code> <i>token</i>
     * provided to determine the possible values.
     * <p>
     * For example, in a Role implementation this method returns
     * all the roles defined in the organization.
     *
     * @param token the <code>SSOToken</code> that is used
     * to determine the possible values
     *
     * @throws SSOException if SSO token is not valid
     * @throws PolicyException if unable to get the list of valid values
     *
     * @return set of valid values
     */
    public ValidValues getValidValues(SSOToken token, String pattern)
            throws SSOException, PolicyException{
        return (new ValidValues(ValidValues.SUCCESS, Collections.EMPTY_SET));
    }

    /**
     * Returns the display name for the value for the given locale.
     * <p>
     * For all the valid values obtained through the
     * <code>getValidValues</code> methods, this method must be called
     * by the GUI and the CLI to get the corresponding display name.
     * <p>
     * The <code>locale</code> variable could be used by the
     * plugin to customize the display name for the given locale.
     * The <code>locale</code> variable could be <code>null</code>,
     * in which case the plugin must use the default locale
     * (most probably en_US).
     * <p>
     * This method returns only the display name and should not
     * be used for the method <code>setValues</code>.
     * <p>
     * Alternatively, if the plugin does not have to localize
     * the value, it can just return the <code>value</code> as is.
     *
     * @param value one of the valid value for the plugin
     * @param locale locale for which the display name must be customized
     *
     * @throws NameNotFoundException if the given <code>value</code>
     * is not one of the valid values for the plugin
     */
    public String getDisplayNameForValue(String value, Locale locale)
            throws NameNotFoundException {
        return value;
    }

    /**
     * Returns the values that were set using the method <code>setValues</code>.
     *
     * @return values that have been set for the user collection
     */
    public Set getValues() {
        return (Collections.EMPTY_SET);
    }

    /**
     * Sets the names for the instance of the <code>Subject</code> object.
     * <p>
     * The names are obtained from the policy object,
     * usually configured when a policy is created.
     * <p>
     * For example, in a Role implementation, this would be name of the role.
     *
     * @param names names selected for the instance of
     * the user collection object
     *
     * @throws InvalidNameException if the given names are not valid
     */
    public void setValues(Set names) throws InvalidNameException {
    }

    /**
     * Determines whether the user belongs to this instance
     * of the <code>Subject</code> object.
     * <p>
     * For example, a Role implementation would return <code>true</code>
     * if the user belongs the specified role; <code>false</code> otherwise.
     *
     * @param token single-sign-on token of the user
     *
     * @return <code>true</code> if the user is member of the given subject;
     * <code>false</code> otherwise
     *
     * @throws SSOException if SSO token is not valid
     * @throws PolicyException if an error occurred while
     * checking if the user is a member of this subject
     */
    public boolean isMember(SSOToken token) 
        throws SSOException, PolicyException {
        return (SSOTokenManager.getInstance().isValidToken(token));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o another object that will be compared with this one
     *
     * @return <code>true</code> if equal; <code>false</code> otherwise
     */
    public boolean equals(Object o) {
        if (o instanceof SampleSubject) {
            return (true);
        }
        return (false);
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a copy of this object
     */
    public Object clone() {
        return (new SampleSubject());
    }
}
