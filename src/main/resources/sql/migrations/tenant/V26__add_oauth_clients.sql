INSERT INTO `oauth_client_details` (`client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `access_token_validity`, `authorities`)
VALUES ('channel-${tenantDatabase}', '${channelClientSecret}', '${identityProviderResourceId},${tenantDatabase}', 'identity', 'client_credentials', ${clientAccessTokenValidity}, 'ALL_FUNCTIONS');
