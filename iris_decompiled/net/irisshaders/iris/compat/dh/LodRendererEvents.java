/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.seibel.distanthorizons.api.DhApi
 *  com.seibel.distanthorizons.api.DhApi$Delayed
 *  com.seibel.distanthorizons.api.enums.rendering.EDhApiFogDrawMode
 *  com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass
 *  com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiGenericObjectShaderProgram
 *  com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterDhInitEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeBufferRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeBufferRenderEvent$EventParam
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeDeferredRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericObjectRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericObjectRenderEvent$EventParam
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericRenderSetupEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderCleanupEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderSetupEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeTextureClearEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiColorDepthTextureCreatedEvent
 *  com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiColorDepthTextureCreatedEvent$EventParam
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam
 *  com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam
 *  com.seibel.distanthorizons.api.objects.DhApiResult
 *  com.seibel.distanthorizons.api.objects.math.DhApiVec3f
 *  com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector
 *  com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.opengl.GL46C
 */
package net.irisshaders.iris.compat.dh;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogDrawMode;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiRenderPass;
import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiGenericObjectShaderProgram;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterDhInitEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeApplyShaderRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeBufferRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeDeferredRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericObjectRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeGenericRenderSetupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderCleanupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderPassEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderSetupEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeTextureClearEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiColorDepthTextureCreatedEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;
import com.seibel.distanthorizons.coreapi.DependencyInjection.OverrideInjector;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.compat.dh.DHCompatInternal;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GL46C;

public class LodRendererEvents {
    private static boolean eventHandlersBound = false;
    private static boolean atTranslucent = false;
    private static int textureWidth;
    private static int textureHeight;

    public static void setupEventHandlers() {
        if (!eventHandlersBound) {
            eventHandlersBound = true;
            Iris.logger.info("Queuing DH event binding...");
            DhApiAfterDhInitEvent beforeCleanupEvent = new DhApiAfterDhInitEvent(){

                public void afterDistantHorizonsInit(DhApiEventParam<Void> event) {
                    Iris.logger.info("DH Ready, binding Iris event handlers...");
                    Iris.loadShaderpackWhenPossible();
                    LodRendererEvents.setupSetDeferredBeforeRenderingEvent();
                    LodRendererEvents.setupReconnectDepthTextureEvent();
                    LodRendererEvents.setupGenericEvent();
                    LodRendererEvents.setupCreateDepthTextureEvent();
                    LodRendererEvents.setupTransparentRendererEventCancling();
                    LodRendererEvents.setupBeforeBufferClearEvent();
                    LodRendererEvents.setupBeforeRenderCleanupEvent();
                    LodRendererEvents.beforeBufferRenderEvent();
                    LodRendererEvents.setupBeforeRenderFrameBufferBinding();
                    LodRendererEvents.setupBeforeRenderPassEvent();
                    LodRendererEvents.setupBeforeApplyShaderEvent();
                    DHCompatInternal.dhEnabled = (Boolean)DhApi.Delayed.configs.graphics().renderingEnabled().getValue();
                    Iris.logger.info("DH Iris events bound.");
                }
            };
            DhApi.events.bind(DhApiAfterDhInitEvent.class, (IBindable)beforeCleanupEvent);
        }
    }

    private static void setupSetDeferredBeforeRenderingEvent() {
        DhApiBeforeRenderEvent beforeRenderEvent = new DhApiBeforeRenderEvent(){

            public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
                DhApi.Delayed.renderProxy.setDeferTransparentRendering(Iris.isPackInUseQuick() && LodRendererEvents.getInstance().shouldOverride);
                DhApi.Delayed.configs.graphics().fog().drawMode().setValue((Object)(LodRendererEvents.getInstance().shouldOverride ? EDhApiFogDrawMode.FOG_DISABLED : EDhApiFogDrawMode.FOG_ENABLED));
            }
        };
        DhApi.events.bind(DhApiBeforeRenderEvent.class, (IBindable)beforeRenderEvent);
    }

    private static void setupReconnectDepthTextureEvent() {
        DhApiBeforeTextureClearEvent beforeRenderEvent = new DhApiBeforeTextureClearEvent(){

            public void beforeClear(DhApiCancelableEventParam<DhApiRenderParam> event) {
                DhApiResult getResult = DhApi.Delayed.renderProxy.getDhDepthTextureId();
                if (getResult.success) {
                    int depthTextureId = (Integer)getResult.payload;
                    LodRendererEvents.getInstance().reconnectDHTextures(depthTextureId);
                }
            }
        };
        DhApi.events.bind(DhApiBeforeTextureClearEvent.class, (IBindable)beforeRenderEvent);
    }

    private static void setupGenericEvent() {
        DhApiBeforeGenericRenderSetupEvent beforeRenderEvent = new DhApiBeforeGenericRenderSetupEvent(){

            public void beforeSetup(DhApiEventParam<DhApiRenderParam> dhApiEventParam) {
                if (LodRendererEvents.getInstance().getGenericFB() != null) {
                    LodRendererEvents.getInstance().getGenericFB().bind();
                }
            }
        };
        DhApiBeforeGenericObjectRenderEvent beforeDrawEvent = new DhApiBeforeGenericObjectRenderEvent(){

            public void beforeRender(DhApiCancelableEventParam<DhApiBeforeGenericObjectRenderEvent.EventParam> dhApiCancelableEventParam) {
                if (((DhApiBeforeGenericObjectRenderEvent.EventParam)dhApiCancelableEventParam.value).resourceLocationPath.equalsIgnoreCase("Clouds") && LodRendererEvents.getInstance().avoidRenderingClouds()) {
                    dhApiCancelableEventParam.cancelEvent();
                }
            }
        };
        DhApi.events.bind(DhApiBeforeGenericRenderSetupEvent.class, (IBindable)beforeRenderEvent);
        DhApi.events.bind(DhApiBeforeGenericObjectRenderEvent.class, (IBindable)beforeDrawEvent);
    }

    private static DHCompatInternal getInstance() {
        return (DHCompatInternal)Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::getDHCompat).map(DHCompat::getInstance).orElse(DHCompatInternal.SHADERLESS);
    }

    private static void setupCreateDepthTextureEvent() {
        DhApiColorDepthTextureCreatedEvent beforeRenderEvent = new DhApiColorDepthTextureCreatedEvent(){

            public void onResize(DhApiEventParam<DhApiColorDepthTextureCreatedEvent.EventParam> input) {
                textureWidth = ((DhApiColorDepthTextureCreatedEvent.EventParam)input.value).newWidth;
                textureHeight = ((DhApiColorDepthTextureCreatedEvent.EventParam)input.value).newHeight;
            }
        };
        DhApi.events.bind(DhApiColorDepthTextureCreatedEvent.class, (IBindable)beforeRenderEvent);
    }

    private static void setupTransparentRendererEventCancling() {
        DhApiBeforeRenderEvent beforeRenderEvent = new DhApiBeforeRenderEvent(){

            public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
                if (ShadowRenderingState.areShadowsCurrentlyBeingRendered() && !LodRendererEvents.getInstance().shouldOverrideShadow) {
                    event.cancelEvent();
                }
            }
        };
        DhApiBeforeDeferredRenderEvent beforeRenderEvent2 = new DhApiBeforeDeferredRenderEvent(){

            public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
                if (ShadowRenderingState.areShadowsCurrentlyBeingRendered() && !LodRendererEvents.getInstance().shouldOverrideShadow) {
                    event.cancelEvent();
                }
            }
        };
        DhApi.events.bind(DhApiBeforeRenderEvent.class, (IBindable)beforeRenderEvent);
        DhApi.events.bind(DhApiBeforeDeferredRenderEvent.class, (IBindable)beforeRenderEvent2);
    }

    private static void setupBeforeRenderCleanupEvent() {
        DhApiBeforeRenderCleanupEvent beforeCleanupEvent = new DhApiBeforeRenderCleanupEvent(){

            public void beforeCleanup(DhApiEventParam<DhApiRenderParam> event) {
                if (LodRendererEvents.getInstance().shouldOverride) {
                    if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                        LodRendererEvents.getInstance().getShadowShader().unbind();
                    } else {
                        LodRendererEvents.getInstance().getSolidShader().unbind();
                    }
                }
            }
        };
        DhApi.events.bind(DhApiBeforeRenderCleanupEvent.class, (IBindable)beforeCleanupEvent);
    }

    private static void setupBeforeBufferClearEvent() {
        DhApiBeforeTextureClearEvent beforeCleanupEvent = new DhApiBeforeTextureClearEvent(){

            public void beforeClear(DhApiCancelableEventParam<DhApiRenderParam> event) {
                if (((DhApiRenderParam)event.value).renderPass == EDhApiRenderPass.OPAQUE) {
                    if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                        event.cancelEvent();
                    } else if (LodRendererEvents.getInstance().shouldOverride) {
                        GL43C.glClear((int)256);
                        event.cancelEvent();
                    }
                }
            }
        };
        DhApi.events.bind(DhApiBeforeTextureClearEvent.class, (IBindable)beforeCleanupEvent);
    }

    private static void beforeBufferRenderEvent() {
        DhApiBeforeBufferRenderEvent beforeCleanupEvent = new DhApiBeforeBufferRenderEvent(){

            public void beforeRender(DhApiEventParam<DhApiBeforeBufferRenderEvent.EventParam> input) {
                DHCompatInternal instance = LodRendererEvents.getInstance();
                if (instance.shouldOverride) {
                    DhApiVec3f modelPos = ((DhApiBeforeBufferRenderEvent.EventParam)input.value).modelPos;
                    if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                        instance.getShadowShader().bind();
                        instance.getShadowShader().setModelPos(modelPos);
                    } else if (atTranslucent) {
                        instance.getTranslucentShader().bind();
                        instance.getTranslucentShader().setModelPos(modelPos);
                    } else {
                        instance.getSolidShader().bind();
                        instance.getSolidShader().setModelPos(modelPos);
                    }
                }
            }
        };
        DhApi.events.bind(DhApiBeforeBufferRenderEvent.class, (IBindable)beforeCleanupEvent);
    }

    private static void setupBeforeRenderFrameBufferBinding() {
        DhApiBeforeRenderSetupEvent beforeRenderPassEvent = new DhApiBeforeRenderSetupEvent(){

            public void beforeSetup(DhApiEventParam<DhApiRenderParam> event) {
                DHCompatInternal instance = LodRendererEvents.getInstance();
                OverrideInjector.INSTANCE.unbind(IDhApiShadowCullingFrustum.class, (IDhApiOverrideable)ShadowRenderer.FRUSTUM);
                OverrideInjector.INSTANCE.unbind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getShadowFBWrapper());
                OverrideInjector.INSTANCE.unbind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getSolidFBWrapper());
                OverrideInjector.INSTANCE.unbind(IDhApiGenericObjectShaderProgram.class, (IDhApiOverrideable)instance.getGenericShader());
                if (instance.shouldOverride) {
                    if (instance.getGenericShader() != null) {
                        OverrideInjector.INSTANCE.bind(IDhApiGenericObjectShaderProgram.class, (IDhApiOverrideable)instance.getGenericShader());
                    }
                    if (ShadowRenderingState.areShadowsCurrentlyBeingRendered() && instance.shouldOverrideShadow) {
                        OverrideInjector.INSTANCE.bind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getShadowFBWrapper());
                        OverrideInjector.INSTANCE.bind(IDhApiShadowCullingFrustum.class, (IDhApiOverrideable)ShadowRenderer.FRUSTUM);
                    } else {
                        OverrideInjector.INSTANCE.bind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getSolidFBWrapper());
                    }
                }
            }
        };
        DhApi.events.bind(DhApiBeforeRenderSetupEvent.class, (IBindable)beforeRenderPassEvent);
    }

    private static void setupBeforeRenderPassEvent() {
        DhApiBeforeRenderPassEvent beforeCleanupEvent = new DhApiBeforeRenderPassEvent(){

            public void beforeRender(DhApiEventParam<DhApiRenderParam> event) {
                float partialTicks;
                DHCompatInternal instance = LodRendererEvents.getInstance();
                if (instance.shouldOverride) {
                    DhApi.Delayed.configs.graphics().ambientOcclusion().enabled().setValue((Object)false);
                    DhApi.Delayed.configs.graphics().fog().drawMode().setValue((Object)EDhApiFogDrawMode.FOG_DISABLED);
                    if (((DhApiRenderParam)event.value).renderPass == EDhApiRenderPass.OPAQUE_AND_TRANSPARENT) {
                        Iris.logger.error("Unexpected; somehow the Opaque + Translucent pass ran with shaders on.");
                    }
                } else {
                    DhApi.Delayed.configs.graphics().ambientOcclusion().enabled().clearValue();
                    DhApi.Delayed.configs.graphics().fog().drawMode().clearValue();
                }
                if (((DhApiRenderParam)event.value).renderPass == EDhApiRenderPass.OPAQUE && instance.shouldOverride) {
                    if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                        instance.getShadowShader().bind();
                    } else {
                        instance.getSolidShader().bind();
                    }
                    atTranslucent = false;
                }
                if (((DhApiRenderParam)event.value).renderPass == EDhApiRenderPass.OPAQUE) {
                    partialTicks = ((DhApiRenderParam)event.value).partialTicks;
                    if (instance.shouldOverride) {
                        if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                            instance.getShadowShader().fillUniformData((Matrix4fc)ShadowRenderer.PROJECTION, (Matrix4fc)ShadowRenderer.MODELVIEW, -1000, partialTicks);
                        } else {
                            Matrix4f projection = CapturedRenderingState.INSTANCE.getGbufferProjection();
                            instance.getSolidShader().fillUniformData((Matrix4fc)new Matrix4f().setPerspective(projection.perspectiveFov(), projection.m11() / projection.m00(), ((DhApiRenderParam)event.value).nearClipPlane, ((DhApiRenderParam)event.value).farClipPlane), (Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferModelView(), -1000, partialTicks);
                        }
                    }
                }
                if (((DhApiRenderParam)event.value).renderPass == EDhApiRenderPass.TRANSPARENT) {
                    partialTicks = ((DhApiRenderParam)event.value).partialTicks;
                    int depthTextureId = (Integer)DhApi.Delayed.renderProxy.getDhDepthTextureId().payload;
                    if (instance.shouldOverrideShadow && ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
                        instance.getShadowShader().bind();
                        instance.getShadowFB().bind();
                        atTranslucent = true;
                        return;
                    }
                    if (instance.shouldOverride && instance.getTranslucentFB() != null) {
                        instance.copyTranslucents(textureWidth, textureHeight);
                        instance.getTranslucentShader().bind();
                        Matrix4f projection = CapturedRenderingState.INSTANCE.getGbufferProjection();
                        GL46C.glDisable((int)2884);
                        instance.getTranslucentShader().fillUniformData((Matrix4fc)new Matrix4f().setPerspective(projection.perspectiveFov(), projection.m11() / projection.m00(), ((DhApiRenderParam)event.value).nearClipPlane, ((DhApiRenderParam)event.value).farClipPlane), (Matrix4fc)CapturedRenderingState.INSTANCE.getGbufferModelView(), -1000, partialTicks);
                        instance.getTranslucentFB().bind();
                    }
                    atTranslucent = true;
                }
            }
        };
        DhApi.events.bind(DhApiBeforeRenderPassEvent.class, (IBindable)beforeCleanupEvent);
    }

    private static void setupBeforeApplyShaderEvent() {
        DhApiBeforeApplyShaderRenderEvent beforeApplyShaderEvent = new DhApiBeforeApplyShaderRenderEvent(){

            public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
                if (Iris.isPackInUseQuick()) {
                    DHCompatInternal instance = LodRendererEvents.getInstance();
                    OverrideInjector.INSTANCE.unbind(IDhApiShadowCullingFrustum.class, (IDhApiOverrideable)ShadowRenderer.FRUSTUM);
                    OverrideInjector.INSTANCE.unbind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getShadowFBWrapper());
                    OverrideInjector.INSTANCE.unbind(IDhApiFramebuffer.class, (IDhApiOverrideable)instance.getSolidFBWrapper());
                    event.cancelEvent();
                }
            }
        };
        DhApi.events.bind(DhApiBeforeApplyShaderRenderEvent.class, (IBindable)beforeApplyShaderEvent);
    }
}

