package com.nt.course_service_lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.course_service_lms.constants.SecurityConstant.*;


///**
// * Authentication filter for service-to-service communication in the LMS system.
// * This filter handles both gateway-routed requests and direct service requests,
// * validating JWT tokens and gateway signatures as appropriate.
// * <p>
// * The filter supports two types of requests:
// * 1. Gateway requests - routed through API Gateway with additional validation headers
// * 2. Direct requests - direct service-to-service calls (when enabled)
// *
// * @author Course Service LMS Team
// * @version 1.0
// * @since 1.0
// */
//@Profile("!test")
//@Component
//public class ServiceAuthenticationFilter extends OncePerRequestFilter {
//
//    /**
//     * Logger instance for this class.
//     */
//    private final Logger logger = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);
//
//    /**
//     * JWT utility for token validation and extraction.
//     */
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    /**
//     * Flag to enable or disable gateway validation.
//     */
//    @Value("${gateway.validation.enabled:true}")
//    private boolean gatewayValidationEnabled;
//
//    /**
//     * Header name for gateway secret validation.
//     */
//    @Value("${gateway.secret.header:X-Gateway-Secret}")
//    private String gatewaySecretHeader;
//
//    /**
//     * Expected value for gateway secret header.
//     */
//    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
//    private String gatewaySecretValue;
//
//    /**
//     * Header name for gateway signature validation.
//     */
//    @Value("${gateway.signature.header:X-Gateway-Signature}")
//    private String gatewaySignatureHeader;
//
//    /**
//     * Expected audience for JWT token validation.
//     */
//    @Value("${spring.application.name}")
//    private String expectedAudience;
//
//    /**
//     * Header name for direct access secret.
//     */
//    @Value("${direct.secret.header:X-Direct-Secret}")
//    private String directSecretHeader;
//
//    /**
//     * Expected value for direct access secret.
//     */
//    @Value("${direct.secret.value:your-direct-secret}")
//    private String directSecretValue;
//
//    /**
//     * Processes each HTTP request through the authentication filter.
//     * Validates tokens and headers based on request type (gateway or direct).
//     *
//     * @param request     the HTTP servlet request
//     * @param response    the HTTP servlet response
//     * @param filterChain the filter chain to continue processing
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException      if an I/O error occurs
//     */
//    @Override
//    protected void doFilterInternal(final HttpServletRequest request,
//                                    final HttpServletResponse response,
//                                    final FilterChain filterChain) throws ServletException, IOException {
//
//
//        try {
//            String path = request.getRequestURI();
//            System.out.println(path);
//            if (path.startsWith("/video/") || path.startsWith("/pdf/") || path.startsWith("/api/service-api/streaming/")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if (path.startsWith("/h2-console") || path.startsWith("/favicon.ico")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if (path.startsWith("/h2-console") || path.startsWith("/favicon.ico")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
//            String clientId = null;
//
//            if (serviceToken != null) {
//                try {
//                    clientId = jwtUtil.extractClientId(serviceToken);
//                } catch (Exception e) {
//                    logger.warn("Failed to extract subject from token: {}", e.getMessage());
//                    handleUnauthorized(response, "failed to fetch the clientId from the token, jwt token is expired");
//                    return;
//                }
//            }
//
//            boolean isGatewayRequest = true;
//            if ("NA".equals(clientId)) {
//                isGatewayRequest = false;
//            }
//
//            logger.debug("Token subject: {}, isGatewayRequest: {}", clientId, isGatewayRequest);
//
//            if (isGate+wayRequest) {
//                if (!handleGatewayRequest(request, response)) {
//                    return;
//                }
//            } else {
//                if (!gatewayValidationEnabled) {
//                    if (!handleDirectRequest(request, response)) {
//                        return;
//                    }
//                } else {
//                    handleForbidden(response, "Direct access not allowed. Please route via API Gateway.");
//                    return;
//                }
//            }
//
//            filterChain.doFilter(request, response);
//
//        } finally {
//            logger.debug("Cleared service token context after request processing");
//        }
//    }
//
//    /**
//     * Handles gateway-routed requests by validating gateway headers and service tokens.
//     *
//     * @param request  the HTTP servlet request
//     * @param response the HTTP servlet response
//     * @return true if the request is valid and should continue, false otherwise
//     * @throws IOException if an I/O error occurs during response writing
//     */
//    private boolean handleGatewayRequest(final HttpServletRequest request, final HttpServletResponse response)
//            throws IOException {
//
//        logger.debug("Processing Gateway Request");
//
//        if (!validateGatewayRequest(request)) {
//            handleForbidden(response, "Gateway request header validation failed.");
//            return false;
//        }
//
//        String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
//        String originalTokenType = request.getHeader(HEADER_X_ORIGINAL_TOKEN_TYPE);
//
//        if (serviceToken == null) {
//            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
//                serviceToken = authHeader.substring(BEARER_PREFIX.length());
//                originalTokenType = TOKEN_TYPE_SERVICE;
//            }
//        }
//
//        if (serviceToken != null && jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
//            setServiceAuthentication(serviceToken, originalTokenType);
//            return true;
//        } else {
//            handleUnauthorized(response, "Invalid or missing service token for gateway request");
//            return false;
//        }
//    }
//
//    /**
//     * Validates gateway request headers including secret, signature, timestamp, and nonce.
//     *
//     * @param request the HTTP servlet request containing gateway headers
//     * @return true if all gateway validation checks pass, false otherwise
//     */
//    private boolean validateGatewayRequest(final HttpServletRequest request) {
//        String gatewaySecret = request.getHeader(gatewaySecretHeader);
//        String signature = request.getHeader(gatewaySignatureHeader);
//        String timestamp = request.getHeader(HEADER_X_GATEWAY_TIMESTAMP);
//        String nonce = request.getHeader(HEADER_X_GATEWAY_NONCE);
//
//        if (!validateSecret(gatewaySecret, gatewaySecretValue)) {
//            logger.warn("Gateway secret validation failed");
//            return false;
//        }
//
//        if (!validateSignature(signature, timestamp, nonce)) {
//            logger.warn("Gateway signature validation failed");
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * Validates that the provided secret header matches the expected secret value.
//     *
//     * @param secretHeader the secret value from the request header
//     * @param secretValue  the expected secret value
//     * @return true if the secrets match, false otherwise
//     */
//    private boolean validateSecret(final String secretHeader, final String secretValue) {
//        return secretValue.equals(secretHeader);
//    }
//
//    /**
//     * Validates the gateway signature using timestamp, nonce, and secret.
//     * Also checks that the request timestamp is within acceptable time bounds.
//     *
//     * @param signature the signature from the request header
//     * @param timestamp the timestamp from the request header
//     * @param nonce     the nonce from the request header
//     * @return true if the signature is valid and timestamp is acceptable, false otherwise
//     */
//    private boolean validateSignature(final String signature, final String timestamp, final String nonce) {
//        if (signature == null || timestamp == null || nonce == null) {
//            return false;
//        }
//        try {
//            long requestTime = Long.parseLong(timestamp);
//            long currentTime = System.currentTimeMillis();
//            long timeDiff = Math.abs(currentTime - requestTime);
//
//            if (timeDiff > MAX_REQUEST_TIME_DIFF_MS) {
//                logger.warn("Request timestamp too old: {}", timeDiff);
//                return false;
//            }
//
//            String expectedSignature = generateSignature(timestamp, nonce, gatewaySecretValue);
//            return signature.equals(expectedSignature);
//
//        } catch (Exception e) {
//            logger.warn("Gateway signature validation error: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * Generates a signature hash based on timestamp, nonce, and secret.
//     *
//     * @param timestamp the request timestamp
//     * @param nonce     the request nonce
//     * @param secret    the secret key for signature generation
//     * @return the generated signature as a string
//     */
//    private String generateSignature(final String timestamp, final String nonce, final String secret) {
//        String data = timestamp + ":" + nonce + ":" + secret;
//        return Integer.toString(data.hashCode());
//    }
//
//    /**
//     * Handles direct service-to-service requests by validating direct secret and service token.
//     *
//     * @param request  the HTTP servlet request
//     * @param response the HTTP servlet response
//     * @return true if the direct request is valid and should continue, false otherwise
//     * @throws IOException if an I/O error occurs during response writing
//     */
//    private boolean handleDirectRequest(final HttpServletRequest request, final HttpServletResponse response)
//            throws IOException {
//
//        logger.debug("Processing Direct Request");
//        String directSecret = request.getHeader(directSecretHeader);
//
//        if (directSecret == null) {
//            handleUnauthorized(response, "Direct secret header is required for direct access");
//            return false;
//        }
//
//        if (!validateSecret(directSecret, directSecretValue)) {
//            handleForbidden(response, "Invalid Direct Secret Header");
//            return false;
//        }
//
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
//            String serviceToken = authHeader.substring(BEARER_PREFIX.length());
//            try {
//                if (jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
//                    setServiceAuthentication(serviceToken, TOKEN_TYPE_SERVICE);
//                    logger.debug("Direct service token stored in context and authentication set");
//                    return true;
//                } else {
//                    handleUnauthorized(response, "Invalid service token for direct access");
//                    return false;
//                }
//            } catch (Exception e) {
//                logger.warn("Direct service token validation failed: {}", e.getMessage());
//                handleUnauthorized(response, "Service token validation failed");
//                return false;
//            }
//        } else {
//            handleUnauthorized(response, "Missing service token in Authorization header for direct access");
//            return false;
//        }
//    }
//
//    /**
//     * Sets the service authentication in the Security Context based on the provided token.
//     * Extracts user information and roles from the token and creates authentication object.
//     *
//     * @param token             the validated service token
//     * @param originalTokenType the original token type from the request
//     */
//    private void setServiceAuthentication(final String token, final String originalTokenType) {
//        List<String> roles = jwtUtil.extractRoles(token);
//        String userId = jwtUtil.extractUserId(token);
//        String userEmail = jwtUtil.extractUserEmail(token);
//        String userFullName = jwtUtil.extractUserFullName(token);
//        List<String> userRoles = jwtUtil.extractUserRoles(token);
//
//        List<SimpleGrantedAuthority> authorities = roles.stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//        if (userRoles != null) {
//            userRoles.stream()
//                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
//                    .map(SimpleGrantedAuthority::new)
//                    .forEach(authorities::add);
//        }
//
//        ServicePrincipal principal = new ServicePrincipal.Builder()
//                .serviceId(jwtUtil.extractClientId(token))
//                .userId(userId)
//                .userEmail(userEmail)
//                .userFullName(userFullName)
//                .originalTokenType(originalTokenType)
//                .build();
//
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(principal, null, authorities);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        logger.debug("Service authentication set for user: {}, service: {}, authorities: {}",
//                userEmail, principal.getServiceId(), authorities);
//    }
//
//    /**
//     * Handles unauthorized requests by setting HTTP 401 status and writing error response.
//     *
//     * @param response the HTTP servlet response
//     * @param message  the error message to include in the response
//     * @throws IOException if an I/O error occurs during response writing
//     */
//    private void handleUnauthorized(final HttpServletResponse response, final String message) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        response.getWriter().write(String.format(ERROR_UNAUTHORIZED, message));
//    }
//
//    /**
//     * Handles forbidden requests by setting HTTP 403 status and writing error response.
//     *
//     * @param response the HTTP servlet response
//     * @param message  the error message to include in the response
//     * @throws IOException if an I/O error occurs during response writing
//     */
//    private void handleForbidden(final HttpServletResponse response, final String message) throws IOException {
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/json");
//        response.getWriter().write(String.format(ERROR_FORBIDDEN, message));
//    }
//
//    /**
//     * Determines whether the filter should be skipped for the given request.
//     * <p>
//     * This implementation skips filtering for requests to the H2 console path.
//     * </p>
//     *
//     * @param request the incoming {@link HttpServletRequest}; must not be null
//     * @return {@code true} if the filter should not be applied; {@code false} otherwise
//     * @throws ServletException if an error occurs during request processing
//     */
//    @Override
//    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI();
//        return path.startsWith("/h2-console");
//    }
//}
/**
 * Authentication filter for service-to-service communication in the LMS system.
 * This filter validates JWT tokens and gateway headers for all requests.
 * <p>
 * The filter enforces gateway validation when enabled, ensuring all requests
 * are properly authenticated through the API Gateway.
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
@Profile("!test")
@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Logger for logging filter activities.
     */
    private final Logger logger = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);

    /**
     * Utility for working with JWT tokens, including validation and claims extraction.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Flag to enable or disable gateway validation. Defaults to true.
     */
    @Value("${gateway.validation.enabled}")
    private boolean gatewayValidationEnabled;

    /**
     * Header name for the gateway secret.
     */
    @Value("${gateway.secret.header}")
    private String gatewaySecretHeader;

    /**
     * Expected secret value for gateway validation.
     */
    @Value("${gateway.secret.value}")
    private String gatewaySecretValue;

    /**
     * Header name for the gateway request signature.
     */
    @Value("${gateway.signature.header}")
    private String gatewaySignatureHeader;

    /**
     * Header name for direct access secret.
     */
    @Value("${direct.secret.header}")
    private String directSecretHeader;

    /**
     * Expected value for direct access secret.
     */
    @Value("${direct.secret.value}")
    private String directSecretValue;

    /**
     * The expected audience claim (application name) for JWT validation.
     */
    @Value("${spring.application.name}")
    private String expectedAudience;

    /**
     * Performs filtering on every request, checking JWT tokens and headers to
     * determine authentication and validity for gateway requests only.
     *
     * @param request     incoming HTTP request
     * @param response    HTTP response
     * @param filterChain chain of filters to continue processing
     * @throws ServletException in case of filter error
     * @throws IOException      in case of I/O error
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        try {
            // Skip filtering for specific paths
            String path = request.getRequestURI();
            if (path.startsWith("/video/") || path.startsWith("/pdf/")
                    || path.startsWith("/api/service-api/streaming/") || path.startsWith("/favicon.ico")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (gatewayValidationEnabled) {
                if (!validateGatewayRequest(request)) {
                    handleForbidden(response, "Invalid or missing gateway authentication headers.");
                    return;
                }
            }
            else {
                if (!handleDirectRequest(request, response)) {
                    handleForbidden(response, "Invalid or missing direct request headers. Please route via API Gateway.");
                    return;
                }
            }

            // Extract service token from Authorization header
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            System.out.println("Authorization Header: " + authHeader);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                handleUnauthorized(response, "Missing or invalid Authorization header");
                return;
            }

            String serviceToken = authHeader.substring(BEARER_PREFIX.length());
            String originalTokenType = request.getHeader(HEADER_X_ORIGINAL_TOKEN_TYPE);

            // Validate service token
            try {
                if (jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
                    // Set authentication context
                    setServiceAuthentication(serviceToken, originalTokenType);
                    logger.debug("Service authentication set successfully for token with audience: {}", expectedAudience);
                } else {
                    handleUnauthorized(response, "Invalid service token for this service");
                    return;
                }
            } catch (Exception e) {
                logger.warn("Service token validation failed: {}", e.getMessage());
                handleUnauthorized(response, "Service token validation failed: " + e.getMessage());
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Unexpected error in authentication filter: {}", e.getMessage());
            handleUnauthorized(response, "Unexpected error: " + e.getMessage());
        } finally {
            logger.debug("Cleared service token context after request processing");
        }
    }

    /**
     * Validates headers in a gateway request.
     *
     * @param request HTTP request
     * @return true if valid, false otherwise
     */
    private boolean validateGatewayRequest(final HttpServletRequest request) {
        String gatewaySecret = request.getHeader(gatewaySecretHeader);
        String signature = request.getHeader(gatewaySignatureHeader);
        String timestamp = request.getHeader(HEADER_X_GATEWAY_TIMESTAMP);
        String nonce = request.getHeader(HEADER_X_GATEWAY_NONCE);

        return validateSecret(gatewaySecret, gatewaySecretValue)
                && validateSignature(signature, timestamp, nonce);
    }

    /**
     * Validates a header-based secret.
     *
     * @param secretHeader the incoming secret
     * @param secretValue  the expected secret
     * @return true if they match, false otherwise
     */
    private boolean validateSecret(final String secretHeader, final String secretValue) {
        return secretValue.equals(secretHeader);
    }

    /**
     * Validates the request signature using timestamp and nonce.
     *
     * @param signature the provided signature
     * @param timestamp the request timestamp
     * @param nonce     a unique request identifier
     * @return true if valid, false otherwise
     */
    private boolean validateSignature(final String signature, final String timestamp, final String nonce) {
        if (signature == null || timestamp == null || nonce == null) {
            return false;
        }

        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            long timeDiff = Math.abs(currentTime - requestTime);

            if (timeDiff > MAX_REQUEST_TIME_DIFF_MS) {
                logger.warn("Request timestamp too old: {}", timeDiff);
                return false;
            }

            String expectedSignature = generateSignature(timestamp, nonce, gatewaySecretValue);
            return signature.equals(expectedSignature);
        } catch (Exception e) {
            logger.warn("Gateway signature validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Generates a signature string from components.
     *
     * @param timestamp timestamp string
     * @param nonce     nonce string
     * @param secret    shared secret
     * @return hash-based signature string
     */
    private String generateSignature(final String timestamp, final String nonce, final String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    /**
     * Handles direct service-to-service requests by validating direct secret and service token.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @return true if the direct request is valid and should continue, false otherwise
     * @throws IOException if an I/O error occurs during response writing
     */
    private boolean handleDirectRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        logger.debug("Processing Direct Request");
        String directSecret = request.getHeader(directSecretHeader);

        if (directSecret == null) {
            handleUnauthorized(response, "Direct secret header is required for direct access");
            return false;
        }

        if (!validateSecret(directSecret, directSecretValue)) {
            handleForbidden(response, "Invalid Direct Secret Header");
            return false;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("Authorization Header: " + authHeader);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String serviceToken = authHeader.substring(BEARER_PREFIX.length());
            System.out.println("Service Token: " + serviceToken);
            try {
                if (jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
                    setServiceAuthentication(serviceToken, TOKEN_TYPE_SERVICE);
                    logger.debug("Direct service token stored in context and authentication set");
                    return true;
                } else {
                    System.out.println("Service Token is invalid");
                    handleUnauthorized(response, "Invalid service token for direct access");
                    return false;
                }
            } catch (Exception e) {
                logger.warn("Direct service token validation failed: {}", e.getMessage());
                handleUnauthorized(response, "Service token validation failed");
                return false;
            }
        } else {
            handleUnauthorized(response, "Missing service token in Authorization header for direct access");
            return false;
        }
    }

    /**
     * Sets the authentication object in the Spring SecurityContext based on token data.
     *
     * @param token             the JWT service token
     * @param originalTokenType the original token type (e.g., ACCESS, SERVICE)
     */
    private void setServiceAuthentication(final String token, final String originalTokenType) {
        List<String> roles = jwtUtil.extractRoles(token);
        String userId = jwtUtil.extractUserId(token);
        String userEmail = jwtUtil.extractUserEmail(token);
        String userFullName = jwtUtil.extractUserFullName(token);
        List<String> userRoles = jwtUtil.extractUserRoles(token);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        if (userRoles != null) {
            userRoles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        ServicePrincipal principal = new ServicePrincipal.Builder()
                .serviceId(jwtUtil.extractClientId(token))
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(userFullName)
                .originalTokenType(originalTokenType)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Service authentication set for user: {}, service: {}, authorities: {}",
                userEmail, principal.getServiceId(), authorities);
    }

    /**
     * Sends an unauthorized (401) response with a JSON error message.
     *
     * @param response HTTP response
     * @param message  the error message to send
     * @throws IOException in case of write failure
     */
    private void handleUnauthorized(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_UNAUTHORIZED, message));
    }

    /**
     * Sends a forbidden (403) response with a JSON error message.
     *
     * @param response HTTP response
     * @param message  the error message to send
     * @throws IOException in case of write failure
     */
    private void handleForbidden(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_FORBIDDEN, message));
    }

    /**
     * Determines whether the filter should be skipped for the given request.
     * <p>
     * This implementation skips filtering for requests to the H2 console path.
     * </p>
     *
     * @param request the incoming {@link HttpServletRequest}; must not be null
     * @return {@code true} if the filter should not be applied; {@code false} otherwise
     * @throws ServletException if an error occurs during request processing
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console");
    }
}
