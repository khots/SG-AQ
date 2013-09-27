package {
import flash.utils.*;
import mx.core.IFlexModuleFactory;
import flash.system.*
import flash.net.registerClassAlias;
import mx.collections.ArrayCollection;
import mx.collections.ArrayList;
import mx.messaging.config.ConfigMap;
import mx.messaging.messages.AcknowledgeMessage;
import mx.messaging.messages.AsyncMessage;
import mx.messaging.messages.CommandMessage;
import mx.messaging.messages.ErrorMessage;
import mx.messaging.messages.MessagePerformanceInfo;
import mx.messaging.messages.RemotingMessage;
import mx.utils.ObjectProxy;
import mx.effects.EffectManager;
import mx.core.mx_internal;

[Mixin]
public class _DAG_FlexInit
{
   public function _DAG_FlexInit()
   {
       super();
   }
   public static function init(fbs:IFlexModuleFactory):void
   {
      EffectManager.mx_internal::registerEffectTrigger("addedEffect", "added");
      EffectManager.mx_internal::registerEffectTrigger("creationCompleteEffect", "creationComplete");
      EffectManager.mx_internal::registerEffectTrigger("focusInEffect", "focusIn");
      EffectManager.mx_internal::registerEffectTrigger("focusOutEffect", "focusOut");
      EffectManager.mx_internal::registerEffectTrigger("hideEffect", "hide");
      EffectManager.mx_internal::registerEffectTrigger("mouseDownEffect", "mouseDown");
      EffectManager.mx_internal::registerEffectTrigger("mouseUpEffect", "mouseUp");
      EffectManager.mx_internal::registerEffectTrigger("moveEffect", "move");
      EffectManager.mx_internal::registerEffectTrigger("removedEffect", "removed");
      EffectManager.mx_internal::registerEffectTrigger("resizeEffect", "resize");
      EffectManager.mx_internal::registerEffectTrigger("resizeEndEffect", "resizeEnd");
      EffectManager.mx_internal::registerEffectTrigger("resizeStartEffect", "resizeStart");
      EffectManager.mx_internal::registerEffectTrigger("rollOutEffect", "rollOut");
      EffectManager.mx_internal::registerEffectTrigger("rollOverEffect", "rollOver");
      EffectManager.mx_internal::registerEffectTrigger("showEffect", "show");
      flash.net.registerClassAlias("flex.messaging.io.ArrayCollection", mx.collections.ArrayCollection);
      flash.net.registerClassAlias("flex.messaging.io.ArrayList", mx.collections.ArrayList);
      flash.net.registerClassAlias("flex.messaging.config.ConfigMap", mx.messaging.config.ConfigMap);
      flash.net.registerClassAlias("flex.messaging.messages.AcknowledgeMessage", mx.messaging.messages.AcknowledgeMessage);
      flash.net.registerClassAlias("flex.messaging.messages.AsyncMessage", mx.messaging.messages.AsyncMessage);
      flash.net.registerClassAlias("flex.messaging.messages.CommandMessage", mx.messaging.messages.CommandMessage);
      flash.net.registerClassAlias("flex.messaging.messages.ErrorMessage", mx.messaging.messages.ErrorMessage);
      flash.net.registerClassAlias("flex.messaging.messages.MessagePerformanceInfo", mx.messaging.messages.MessagePerformanceInfo);
      flash.net.registerClassAlias("flex.messaging.messages.RemotingMessage", mx.messaging.messages.RemotingMessage);
      flash.net.registerClassAlias("flex.messaging.io.ObjectProxy", mx.utils.ObjectProxy);
      var styleNames:Array = ["fontWeight", "modalTransparencyBlur", "rollOverColor", "textRollOverColor", "verticalGridLineColor", "backgroundDisabledColor", "textIndent", "barColor", "fontSize", "kerning", "footerColors", "textAlign", "fontStyle", "dropdownBorderColor", "modalTransparencyDuration", "textSelectedColor", "horizontalGridLineColor", "todayColor", "selectionColor", "modalTransparency", "fontGridFitType", "selectionDisabledColor", "disabledColor", "fontAntiAliasType", "modalTransparencyColor", "alternatingItemColors", "dropShadowColor", "themeColor", "letterSpacing", "fontFamily", "color", "fontThickness", "errorColor", "headerColors", "fontSharpness"];

      import mx.styles.StyleManager;

      for (var i:int = 0; i < styleNames.length; i++)
      {
         StyleManager.registerInheritingStyle(styleNames[i]);
      }
   }
}  // FlexInit
}  // package
