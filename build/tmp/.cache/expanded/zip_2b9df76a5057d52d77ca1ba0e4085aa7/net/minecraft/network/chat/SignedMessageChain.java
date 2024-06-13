package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {
    static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    SignedMessageLink nextLink;
    Instant f_302755_ = Instant.EPOCH;

    public SignedMessageChain(UUID pSender, UUID pSessionId) {
        this.nextLink = SignedMessageLink.root(pSender, pSessionId);
    }

    public SignedMessageChain.Encoder encoder(Signer pSigner) {
        return p_326076_ -> {
            SignedMessageLink signedmessagelink = this.nextLink;
            if (signedmessagelink == null) {
                return null;
            } else {
                this.nextLink = signedmessagelink.advance();
                return new MessageSignature(pSigner.sign(p_248065_ -> PlayerChatMessage.updateSignature(p_248065_, signedmessagelink, p_326076_)));
            }
        };
    }

    public SignedMessageChain.Decoder decoder(final ProfilePublicKey pPublicKey) {
        final SignatureValidator signaturevalidator = pPublicKey.createSignatureValidator();
        return new SignedMessageChain.Decoder() {
            @Override
            public PlayerChatMessage unpack(@Nullable MessageSignature p_328199_, SignedMessageBody p_328915_) throws SignedMessageChain.DecodeException {
                if (p_328199_ == null) {
                    throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_315060_);
                } else if (pPublicKey.data().hasExpired()) {
                    throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_314329_);
                } else {
                    SignedMessageLink signedmessagelink = SignedMessageChain.this.nextLink;
                    if (signedmessagelink == null) {
                        throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_316173_);
                    } else if (p_328915_.timeStamp().isBefore(SignedMessageChain.this.f_302755_)) {
                        this.m_320632_();
                        throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_315233_);
                    } else {
                        SignedMessageChain.this.f_302755_ = p_328915_.timeStamp();
                        PlayerChatMessage playerchatmessage = new PlayerChatMessage(signedmessagelink, p_328199_, p_328915_, null, FilterMask.PASS_THROUGH);
                        if (!playerchatmessage.verify(signaturevalidator)) {
                            this.m_320632_();
                            throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_314709_);
                        } else {
                            if (playerchatmessage.hasExpiredServer(Instant.now())) {
                                SignedMessageChain.LOGGER
                                    .warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", p_328915_.content());
                            }

                            SignedMessageChain.this.nextLink = signedmessagelink.advance();
                            return playerchatmessage;
                        }
                    }
                }
            }

            @Override
            public void m_320632_() {
                SignedMessageChain.this.nextLink = null;
            }
        };
    }

    public static class DecodeException extends ThrowingComponent {
        static final Component f_315060_ = Component.translatable("chat.disabled.missingProfileKey");
        static final Component f_316173_ = Component.translatable("chat.disabled.chain_broken");
        static final Component f_314329_ = Component.translatable("chat.disabled.expiredProfileKey");
        static final Component f_314709_ = Component.translatable("chat.disabled.invalid_signature");
        static final Component f_315233_ = Component.translatable("chat.disabled.out_of_order_chat");

        public DecodeException(Component pComponent) {
            super(pComponent);
        }
    }

    @FunctionalInterface
    public interface Decoder {
        static SignedMessageChain.Decoder unsigned(UUID pSender, BooleanSupplier p_312636_) {
            return (p_326079_, p_326080_) -> {
                if (p_312636_.getAsBoolean()) {
                    throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.f_315060_);
                } else {
                    return PlayerChatMessage.unsigned(pSender, p_326080_.content());
                }
            };
        }

        PlayerChatMessage unpack(@Nullable MessageSignature pSignature, SignedMessageBody pBody) throws SignedMessageChain.DecodeException;

        default void m_320632_() {
        }
    }

    @FunctionalInterface
    public interface Encoder {
        SignedMessageChain.Encoder UNSIGNED = p_250548_ -> null;

        @Nullable
        MessageSignature pack(SignedMessageBody pBody);
    }
}