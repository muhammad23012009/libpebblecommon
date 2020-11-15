package io.rebble.libpebblecommon.packets

import assertIs
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import kotlin.test.Test
import kotlin.test.assertEquals


internal class SystemMessageTest {
    @Test
    fun serializeDeserializeAppVersionRequest() {
        val originalMessage = PhoneAppVersion.AppVersionRequest()

        val bytes = originalMessage.serialize()
        val newMessage = PebblePacket.deserialize(bytes)

        assertIs<PhoneAppVersion.AppVersionRequest>(newMessage)
    }

    @Test
    fun serializeDeserializeAppVersionResponse() {
        val expectedPlatformFlags = listOf(
            PhoneAppVersion.PlatformFlag.Accelerometer,
            PhoneAppVersion.PlatformFlag.GPS,
            PhoneAppVersion.PlatformFlag.SMS,
            PhoneAppVersion.PlatformFlag.Telephony
        )

        val expectedProtocolCaps = listOf(
            PhoneAppVersion.ProtocolCapsFlag.SupportsAppRunStateProtocol,
            PhoneAppVersion.ProtocolCapsFlag.SupportsHealthInsights,
            PhoneAppVersion.ProtocolCapsFlag.SupportsLocalization,
            PhoneAppVersion.ProtocolCapsFlag.SupportsUnreadCoreDump,
            PhoneAppVersion.ProtocolCapsFlag.SupportsWorkoutApp
        )

        val originalMessage = PhoneAppVersion.AppVersionResponse(
            70u,
            0u,
            PhoneAppVersion.PlatformFlag.makeFlags(
                PhoneAppVersion.OSType.Linux,
                expectedPlatformFlags
            ),
            7u,
            12u,
            15u,
            1u,
            PhoneAppVersion.ProtocolCapsFlag.makeFlags(expectedProtocolCaps)
        )

        val bytes = originalMessage.serialize()
        val newMessage = PebblePacket.deserialize(bytes)

        assertIs<PhoneAppVersion.AppVersionResponse>(newMessage)

        assertEquals(70u, newMessage.protocolVersion.get())
        // Convert flags to set since we don't care about the order
        assertEquals(
            expectedPlatformFlags.toSet(),
            PhoneAppVersion.PlatformFlag.fromFlags(newMessage.platformFlags.get()).second.toSet()
        )
        assertEquals(
            PhoneAppVersion.OSType.Linux,
            PhoneAppVersion.PlatformFlag.fromFlags(newMessage.platformFlags.get()).first
        )
        assertEquals(7u, newMessage.responseVersion.get())
        assertEquals(12u, newMessage.majorVersion.get())
        assertEquals(15u, newMessage.minorVersion.get())
        assertEquals(
            expectedProtocolCaps.toSet(),
            PhoneAppVersion.ProtocolCapsFlag.fromFlags(newMessage.protocolCaps.get()).toSet()
        )
    }
}