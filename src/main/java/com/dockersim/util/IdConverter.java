package com.dockersim.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IdConverter {

    /**
     * Long ID를 UUID로 변환
     */
    public static UUID toUUID(Long id) {
        if (id == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(0L);  // 앞 8바이트는 0으로
        bb.putLong(id);  // 뒤 8바이트에 ID 저장
        return UUID.nameUUIDFromBytes(bb.array());
    }

    /**
     * UUID를 Long ID로 변환
     */
    public static Long toLong(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.position(8); // 뒤 8바이트만 읽기
        return bb.getLong();
    }
}
