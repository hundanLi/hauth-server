package com.hauth.cas.constant;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/15 19:39
 */
public interface ErrorCodeConstant {

    /**
     * INVALID_REQUEST - not all of the required request parameters were present
     * <p>
     * INVALID_TICKET_SPEC - failure to meet the requirements of validation specification
     * <p>
     * UNAUTHORIZED_SERVICE_PROXY - the service is not authorized to perform proxy authentication
     * <p>
     * INVALID_PROXY_CALLBACK - The proxy callback specified is invalid. The credentials specified for proxy authentication do not meet the security requirements
     * <p>
     * INVALID_TICKET - the ticket provided was not valid, or the ticket did not come from an initial login and renew was set on validation. The body of the <cas:authenticationFailure> block of the XML response SHOULD describe the exact details.
     * <p>
     * INVALID_SERVICE - the ticket provided was valid, but the service specified did not match the service associated with the ticket. CAS MUST invalidate the ticket and disallow future validation of that same ticket.
     * <p>
     * INTERNAL_ERROR - an internal error occurred during ticket validation
     */

    String INVALID_REQUEST = "INVALID_REQUEST";
    String INVALID_TICKET_SPEC = "INVALID_TICKET_SPEC";
    String UNAUTHORIZED_SERVICE_PROXY = "UNAUTHORIZED_SERVICE_PROXY";
    String INVALID_PROXY_CALLBACK = "INVALID_PROXY_CALLBACK";
    String INVALID_TICKET = "INVALID_TICKET";
    String INVALID_SERVICE = "INVALID_SERVICE";
    String INTERNAL_ERROR = "INTERNAL_ERROR";
    String INVALID_CREDENTIAL = "INVALID_CREDENTIAL";

}
