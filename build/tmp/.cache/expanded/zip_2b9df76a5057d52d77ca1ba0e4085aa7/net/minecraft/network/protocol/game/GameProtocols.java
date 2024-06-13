package net.minecraft.network.protocol.game;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.CommonPacketTypes;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.CookiePacketTypes;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.PingPacketTypes;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

public class GameProtocols {
    public static final ProtocolInfo.Unbound<ServerGamePacketListener, RegistryFriendlyByteBuf> f_315992_ = ProtocolInfoBuilder.m_321568_(
        ConnectionProtocol.PLAY,
        p_329121_ -> p_329121_.m_322062_(GamePacketTypes.f_314386_, ServerboundAcceptTeleportationPacket.f_317145_)
                .m_322062_(GamePacketTypes.f_316436_, ServerboundBlockEntityTagQueryPacket.f_316461_)
                .m_322062_(GamePacketTypes.f_314929_, ServerboundChangeDifficultyPacket.f_314182_)
                .m_322062_(GamePacketTypes.f_316322_, ServerboundChatAckPacket.f_314055_)
                .m_322062_(GamePacketTypes.f_315003_, ServerboundChatCommandPacket.f_316859_)
                .m_322062_(GamePacketTypes.f_315616_, ServerboundChatCommandSignedPacket.f_313935_)
                .m_322062_(GamePacketTypes.f_315716_, ServerboundChatPacket.f_315924_)
                .m_322062_(GamePacketTypes.f_315111_, ServerboundChatSessionUpdatePacket.f_314546_)
                .m_322062_(GamePacketTypes.f_315939_, ServerboundChunkBatchReceivedPacket.f_314590_)
                .m_322062_(GamePacketTypes.f_314416_, ServerboundClientCommandPacket.f_315392_)
                .m_322062_(CommonPacketTypes.f_314731_, ServerboundClientInformationPacket.f_315737_)
                .m_322062_(GamePacketTypes.f_314601_, ServerboundCommandSuggestionPacket.f_316231_)
                .m_322062_(GamePacketTypes.f_315986_, ServerboundConfigurationAcknowledgedPacket.f_314723_)
                .m_322062_(GamePacketTypes.f_316494_, ServerboundContainerButtonClickPacket.f_316578_)
                .m_322062_(GamePacketTypes.f_317033_, ServerboundContainerClickPacket.f_316870_)
                .m_322062_(GamePacketTypes.f_315682_, ServerboundContainerClosePacket.f_315426_)
                .m_322062_(GamePacketTypes.f_314574_, ServerboundContainerSlotStateChangedPacket.f_314655_)
                .m_322062_(CookiePacketTypes.f_316493_, ServerboundCookieResponsePacket.f_316817_)
                .m_322062_(CommonPacketTypes.f_314805_, ServerboundCustomPayloadPacket.f_316323_)
                .m_322062_(GamePacketTypes.f_315236_, ServerboundDebugSampleSubscriptionPacket.f_314211_)
                .m_322062_(GamePacketTypes.f_316788_, ServerboundEditBookPacket.f_316818_)
                .m_322062_(GamePacketTypes.f_314400_, ServerboundEntityTagQueryPacket.f_315919_)
                .m_322062_(GamePacketTypes.f_317158_, ServerboundInteractPacket.f_316172_)
                .m_322062_(GamePacketTypes.f_316875_, ServerboundJigsawGeneratePacket.f_316178_)
                .m_322062_(CommonPacketTypes.f_316196_, ServerboundKeepAlivePacket.f_315984_)
                .m_322062_(GamePacketTypes.f_315556_, ServerboundLockDifficultyPacket.f_314910_)
                .m_322062_(GamePacketTypes.f_317056_, ServerboundMovePlayerPacket.Pos.f_314397_)
                .m_322062_(GamePacketTypes.f_314798_, ServerboundMovePlayerPacket.PosRot.f_316356_)
                .m_322062_(GamePacketTypes.f_316888_, ServerboundMovePlayerPacket.Rot.f_315704_)
                .m_322062_(GamePacketTypes.f_314981_, ServerboundMovePlayerPacket.StatusOnly.f_316091_)
                .m_322062_(GamePacketTypes.f_316557_, ServerboundMoveVehiclePacket.f_315510_)
                .m_322062_(GamePacketTypes.f_316162_, ServerboundPaddleBoatPacket.f_316326_)
                .m_322062_(GamePacketTypes.f_317093_, ServerboundPickItemPacket.f_315445_)
                .m_322062_(PingPacketTypes.f_314322_, ServerboundPingRequestPacket.f_314691_)
                .m_322062_(GamePacketTypes.f_315108_, ServerboundPlaceRecipePacket.f_316340_)
                .m_322062_(GamePacketTypes.f_314402_, ServerboundPlayerAbilitiesPacket.f_314572_)
                .m_322062_(GamePacketTypes.f_314753_, ServerboundPlayerActionPacket.f_315134_)
                .m_322062_(GamePacketTypes.f_316884_, ServerboundPlayerCommandPacket.f_315749_)
                .m_322062_(GamePacketTypes.f_315317_, ServerboundPlayerInputPacket.f_314428_)
                .m_322062_(CommonPacketTypes.f_316824_, ServerboundPongPacket.f_315035_)
                .m_322062_(GamePacketTypes.f_317047_, ServerboundRecipeBookChangeSettingsPacket.f_316483_)
                .m_322062_(GamePacketTypes.f_315800_, ServerboundRecipeBookSeenRecipePacket.f_317003_)
                .m_322062_(GamePacketTypes.f_315131_, ServerboundRenameItemPacket.f_314151_)
                .m_322062_(CommonPacketTypes.f_316458_, ServerboundResourcePackPacket.f_316982_)
                .m_322062_(GamePacketTypes.f_315067_, ServerboundSeenAdvancementsPacket.f_314435_)
                .m_322062_(GamePacketTypes.f_315356_, ServerboundSelectTradePacket.f_315957_)
                .m_322062_(GamePacketTypes.f_317052_, ServerboundSetBeaconPacket.f_314389_)
                .m_322062_(GamePacketTypes.f_316312_, ServerboundSetCarriedItemPacket.f_314858_)
                .m_322062_(GamePacketTypes.f_316399_, ServerboundSetCommandBlockPacket.f_315336_)
                .m_322062_(GamePacketTypes.f_315305_, ServerboundSetCommandMinecartPacket.f_313905_)
                .m_322062_(GamePacketTypes.f_316927_, ServerboundSetCreativeModeSlotPacket.f_316304_)
                .m_322062_(GamePacketTypes.f_315385_, ServerboundSetJigsawBlockPacket.f_316452_)
                .m_322062_(GamePacketTypes.f_316388_, ServerboundSetStructureBlockPacket.f_313925_)
                .m_322062_(GamePacketTypes.f_315193_, ServerboundSignUpdatePacket.f_315081_)
                .m_322062_(GamePacketTypes.f_315113_, ServerboundSwingPacket.f_315791_)
                .m_322062_(GamePacketTypes.f_314602_, ServerboundTeleportToEntityPacket.f_316686_)
                .m_322062_(GamePacketTypes.f_316260_, ServerboundUseItemOnPacket.f_316673_)
                .m_322062_(GamePacketTypes.f_316696_, ServerboundUseItemPacket.f_316479_)
    );
    public static final ProtocolInfo.Unbound<ClientGamePacketListener, RegistryFriendlyByteBuf> f_315024_ = ProtocolInfoBuilder.m_323393_(
        ConnectionProtocol.PLAY,
        p_332342_ -> p_332342_.m_319612_(GamePacketTypes.f_316530_, ClientboundBundlePacket::new, new ClientboundBundleDelimiterPacket())
                .m_322062_(GamePacketTypes.f_316505_, ClientboundAddEntityPacket.f_317084_)
                .m_322062_(GamePacketTypes.f_316938_, ClientboundAddExperienceOrbPacket.f_313959_)
                .m_322062_(GamePacketTypes.f_316290_, ClientboundAnimatePacket.f_315429_)
                .m_322062_(GamePacketTypes.f_313912_, ClientboundAwardStatsPacket.f_314726_)
                .m_322062_(GamePacketTypes.f_315632_, ClientboundBlockChangedAckPacket.f_314302_)
                .m_322062_(GamePacketTypes.f_316424_, ClientboundBlockDestructionPacket.f_316887_)
                .m_322062_(GamePacketTypes.f_316158_, ClientboundBlockEntityDataPacket.f_315210_)
                .m_322062_(GamePacketTypes.f_314776_, ClientboundBlockEventPacket.f_316965_)
                .m_322062_(GamePacketTypes.f_314104_, ClientboundBlockUpdatePacket.f_314688_)
                .m_322062_(GamePacketTypes.f_315717_, ClientboundBossEventPacket.f_314786_)
                .m_322062_(GamePacketTypes.f_316656_, ClientboundChangeDifficultyPacket.f_316793_)
                .m_322062_(GamePacketTypes.f_316747_, ClientboundChunkBatchFinishedPacket.f_315055_)
                .m_322062_(GamePacketTypes.f_316085_, ClientboundChunkBatchStartPacket.f_316871_)
                .m_322062_(GamePacketTypes.f_316412_, ClientboundChunksBiomesPacket.f_313994_)
                .m_322062_(GamePacketTypes.f_314083_, ClientboundClearTitlesPacket.f_314613_)
                .m_322062_(GamePacketTypes.f_314280_, ClientboundCommandSuggestionsPacket.f_315522_)
                .m_322062_(GamePacketTypes.f_315127_, ClientboundCommandsPacket.f_316838_)
                .m_322062_(GamePacketTypes.f_316559_, ClientboundContainerClosePacket.f_316790_)
                .m_322062_(GamePacketTypes.f_315600_, ClientboundContainerSetContentPacket.f_315707_)
                .m_322062_(GamePacketTypes.f_314542_, ClientboundContainerSetDataPacket.f_315733_)
                .m_322062_(GamePacketTypes.f_315498_, ClientboundContainerSetSlotPacket.f_313946_)
                .m_322062_(CookiePacketTypes.f_314706_, ClientboundCookieRequestPacket.f_314850_)
                .m_322062_(GamePacketTypes.f_316416_, ClientboundCooldownPacket.f_315282_)
                .m_322062_(GamePacketTypes.f_316409_, ClientboundCustomChatCompletionsPacket.f_316575_)
                .m_322062_(CommonPacketTypes.f_314728_, ClientboundCustomPayloadPacket.f_314159_)
                .m_322062_(GamePacketTypes.f_316188_, ClientboundDamageEventPacket.f_315470_)
                .m_322062_(GamePacketTypes.f_316726_, ClientboundDebugSamplePacket.f_316133_)
                .m_322062_(GamePacketTypes.f_315753_, ClientboundDeleteChatPacket.f_315431_)
                .m_322062_(CommonPacketTypes.f_314152_, ClientboundDisconnectPacket.f_315680_)
                .m_322062_(GamePacketTypes.f_316364_, ClientboundDisguisedChatPacket.f_314761_)
                .m_322062_(GamePacketTypes.f_316213_, ClientboundEntityEventPacket.f_316247_)
                .m_322062_(GamePacketTypes.f_316834_, ClientboundExplodePacket.f_316417_)
                .m_322062_(GamePacketTypes.f_314033_, ClientboundForgetLevelChunkPacket.f_315427_)
                .m_322062_(GamePacketTypes.f_315774_, ClientboundGameEventPacket.f_315994_)
                .m_322062_(GamePacketTypes.f_314122_, ClientboundHorseScreenOpenPacket.f_314879_)
                .m_322062_(GamePacketTypes.f_315718_, ClientboundHurtAnimationPacket.f_314121_)
                .m_322062_(GamePacketTypes.f_314453_, ClientboundInitializeBorderPacket.f_314827_)
                .m_322062_(CommonPacketTypes.f_314082_, ClientboundKeepAlivePacket.f_314816_)
                .m_322062_(GamePacketTypes.f_314368_, ClientboundLevelChunkWithLightPacket.f_315112_)
                .m_322062_(GamePacketTypes.f_314846_, ClientboundLevelEventPacket.f_315017_)
                .m_322062_(GamePacketTypes.f_316246_, ClientboundLevelParticlesPacket.f_316628_)
                .m_322062_(GamePacketTypes.f_314315_, ClientboundLightUpdatePacket.f_316861_)
                .m_322062_(GamePacketTypes.f_315761_, ClientboundLoginPacket.f_316307_)
                .m_322062_(GamePacketTypes.f_314260_, ClientboundMapItemDataPacket.f_314295_)
                .m_322062_(GamePacketTypes.f_313974_, ClientboundMerchantOffersPacket.f_315243_)
                .m_322062_(GamePacketTypes.f_314503_, ClientboundMoveEntityPacket.Pos.f_314194_)
                .m_322062_(GamePacketTypes.f_315449_, ClientboundMoveEntityPacket.PosRot.f_317049_)
                .m_322062_(GamePacketTypes.f_314818_, ClientboundMoveEntityPacket.Rot.f_315641_)
                .m_322062_(GamePacketTypes.f_315871_, ClientboundMoveVehiclePacket.f_314203_)
                .m_322062_(GamePacketTypes.f_314099_, ClientboundOpenBookPacket.f_316297_)
                .m_322062_(GamePacketTypes.f_316807_, ClientboundOpenScreenPacket.f_314162_)
                .m_322062_(GamePacketTypes.f_315059_, ClientboundOpenSignEditorPacket.f_316511_)
                .m_322062_(CommonPacketTypes.f_316690_, ClientboundPingPacket.f_314346_)
                .m_322062_(PingPacketTypes.f_316097_, ClientboundPongResponsePacket.f_314698_)
                .m_322062_(GamePacketTypes.f_314098_, ClientboundPlaceGhostRecipePacket.f_315813_)
                .m_322062_(GamePacketTypes.f_316146_, ClientboundPlayerAbilitiesPacket.f_315591_)
                .m_322062_(GamePacketTypes.f_315278_, ClientboundPlayerChatPacket.f_314263_)
                .m_322062_(GamePacketTypes.f_316020_, ClientboundPlayerCombatEndPacket.f_315956_)
                .m_322062_(GamePacketTypes.f_315032_, ClientboundPlayerCombatEnterPacket.f_314465_)
                .m_322062_(GamePacketTypes.f_316354_, ClientboundPlayerCombatKillPacket.f_316921_)
                .m_322062_(GamePacketTypes.f_316010_, ClientboundPlayerInfoRemovePacket.f_314830_)
                .m_322062_(GamePacketTypes.f_315640_, ClientboundPlayerInfoUpdatePacket.f_315507_)
                .m_322062_(GamePacketTypes.f_314591_, ClientboundPlayerLookAtPacket.f_315482_)
                .m_322062_(GamePacketTypes.f_315547_, ClientboundPlayerPositionPacket.f_313896_)
                .m_322062_(GamePacketTypes.f_314393_, ClientboundRecipePacket.f_316105_)
                .m_322062_(GamePacketTypes.f_315935_, ClientboundRemoveEntitiesPacket.f_315069_)
                .m_322062_(GamePacketTypes.f_316638_, ClientboundRemoveMobEffectPacket.f_314015_)
                .m_322062_(GamePacketTypes.f_315844_, ClientboundResetScorePacket.f_314577_)
                .m_322062_(CommonPacketTypes.f_315299_, ClientboundResourcePackPopPacket.f_314321_)
                .m_322062_(CommonPacketTypes.f_316687_, ClientboundResourcePackPushPacket.f_314484_)
                .m_322062_(GamePacketTypes.f_313964_, ClientboundRespawnPacket.f_314716_)
                .m_322062_(GamePacketTypes.f_316835_, ClientboundRotateHeadPacket.f_315782_)
                .m_322062_(GamePacketTypes.f_315721_, ClientboundSectionBlocksUpdatePacket.f_315562_)
                .m_322062_(GamePacketTypes.f_315708_, ClientboundSelectAdvancementsTabPacket.f_315979_)
                .m_322062_(GamePacketTypes.f_314766_, ClientboundServerDataPacket.f_316752_)
                .m_322062_(GamePacketTypes.f_315824_, ClientboundSetActionBarTextPacket.f_317133_)
                .m_322062_(GamePacketTypes.f_314106_, ClientboundSetBorderCenterPacket.f_314354_)
                .m_322062_(GamePacketTypes.f_314619_, ClientboundSetBorderLerpSizePacket.f_315852_)
                .m_322062_(GamePacketTypes.f_315074_, ClientboundSetBorderSizePacket.f_316063_)
                .m_322062_(GamePacketTypes.f_315324_, ClientboundSetBorderWarningDelayPacket.f_316106_)
                .m_322062_(GamePacketTypes.f_314899_, ClientboundSetBorderWarningDistancePacket.f_316932_)
                .m_322062_(GamePacketTypes.f_315911_, ClientboundSetCameraPacket.f_316363_)
                .m_322062_(GamePacketTypes.f_315363_, ClientboundSetCarriedItemPacket.f_315552_)
                .m_322062_(GamePacketTypes.f_314771_, ClientboundSetChunkCacheCenterPacket.f_316858_)
                .m_322062_(GamePacketTypes.f_314887_, ClientboundSetChunkCacheRadiusPacket.f_315586_)
                .m_322062_(GamePacketTypes.f_315211_, ClientboundSetDefaultSpawnPositionPacket.f_314422_)
                .m_322062_(GamePacketTypes.f_315256_, ClientboundSetDisplayObjectivePacket.f_315391_)
                .m_322062_(GamePacketTypes.f_314449_, ClientboundSetEntityDataPacket.f_316233_)
                .m_322062_(GamePacketTypes.f_314312_, ClientboundSetEntityLinkPacket.f_314212_)
                .m_322062_(GamePacketTypes.f_313933_, ClientboundSetEntityMotionPacket.f_316490_)
                .m_322062_(GamePacketTypes.f_314487_, ClientboundSetEquipmentPacket.f_314063_)
                .m_322062_(GamePacketTypes.f_315083_, ClientboundSetExperiencePacket.f_316675_)
                .m_322062_(GamePacketTypes.f_315504_, ClientboundSetHealthPacket.f_315208_)
                .m_322062_(GamePacketTypes.f_317023_, ClientboundSetObjectivePacket.f_315966_)
                .m_322062_(GamePacketTypes.f_314908_, ClientboundSetPassengersPacket.f_316682_)
                .m_322062_(GamePacketTypes.f_316404_, ClientboundSetPlayerTeamPacket.f_315711_)
                .m_322062_(GamePacketTypes.f_315384_, ClientboundSetScorePacket.f_316191_)
                .m_322062_(GamePacketTypes.f_314752_, ClientboundSetSimulationDistancePacket.f_314799_)
                .m_322062_(GamePacketTypes.f_315692_, ClientboundSetSubtitleTextPacket.f_315951_)
                .m_322062_(GamePacketTypes.f_316320_, ClientboundSetTimePacket.f_315948_)
                .m_322062_(GamePacketTypes.f_315546_, ClientboundSetTitleTextPacket.f_317155_)
                .m_322062_(GamePacketTypes.f_314485_, ClientboundSetTitlesAnimationPacket.f_317008_)
                .m_322062_(GamePacketTypes.f_315878_, ClientboundSoundEntityPacket.f_316622_)
                .m_322062_(GamePacketTypes.f_315518_, ClientboundSoundPacket.f_314253_)
                .m_322062_(GamePacketTypes.f_315258_, ClientboundStartConfigurationPacket.f_314184_)
                .m_322062_(GamePacketTypes.f_316904_, ClientboundStopSoundPacket.f_314855_)
                .m_322062_(CommonPacketTypes.f_313908_, ClientboundStoreCookiePacket.f_313911_)
                .m_322062_(GamePacketTypes.f_317115_, ClientboundSystemChatPacket.f_314993_)
                .m_322062_(GamePacketTypes.f_313977_, ClientboundTabListPacket.f_314256_)
                .m_322062_(GamePacketTypes.f_315760_, ClientboundTagQueryPacket.f_315596_)
                .m_322062_(GamePacketTypes.f_316052_, ClientboundTakeItemEntityPacket.f_315827_)
                .m_322062_(GamePacketTypes.f_316799_, ClientboundTeleportEntityPacket.f_315967_)
                .m_322062_(GamePacketTypes.f_313968_, ClientboundTickingStatePacket.f_316389_)
                .m_322062_(GamePacketTypes.f_315414_, ClientboundTickingStepPacket.f_315331_)
                .m_322062_(CommonPacketTypes.f_316077_, ClientboundTransferPacket.f_316509_)
                .m_322062_(GamePacketTypes.f_315922_, ClientboundUpdateAdvancementsPacket.f_315601_)
                .m_322062_(GamePacketTypes.f_315098_, ClientboundUpdateAttributesPacket.f_316910_)
                .m_322062_(GamePacketTypes.f_314065_, ClientboundUpdateMobEffectPacket.f_314852_)
                .m_322062_(GamePacketTypes.f_316403_, ClientboundUpdateRecipesPacket.f_315247_)
                .m_322062_(CommonPacketTypes.f_314377_, ClientboundUpdateTagsPacket.f_313944_)
                .m_322062_(GamePacketTypes.f_316345_, ClientboundProjectilePowerPacket.f_314474_)
    );
}