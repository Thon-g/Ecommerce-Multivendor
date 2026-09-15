package com.abs.app.domain.service;

public interface RefreshTokenService {
    void save(String userId, String familyId, String tokenId, long expirationMinutes);

    String get(String userId, String familyId);

    void invalidateFamily(String userId, String familyId);

    void invalidateAll(String userId);

    boolean rotate(String userId, String familyId, String oldTokenId, String newTokenId, long expirationMinutes);
}
