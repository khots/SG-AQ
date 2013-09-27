package 
{

import flash.display.Sprite;
import mx.core.IFlexModuleFactory;
import mx.core.mx_internal;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

[ExcludeClass]

public class _TitleWindowStyle
{
    [Embed(_pathsep='true', _resolvedSource='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', source='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', _file='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$defaults.css', symbol='CloseButtonDisabled', _line='1408')]
    private static var _embed_css_Assets_swf_CloseButtonDisabled_1090433878:Class;
    [Embed(_pathsep='true', _resolvedSource='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', source='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', _file='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$defaults.css', symbol='CloseButtonUp', _line='1411')]
    private static var _embed_css_Assets_swf_CloseButtonUp_270378229:Class;
    [Embed(_pathsep='true', _resolvedSource='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', source='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', _file='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$defaults.css', symbol='CloseButtonDown', _line='1409')]
    private static var _embed_css_Assets_swf_CloseButtonDown_1436987280:Class;
    [Embed(_pathsep='true', _resolvedSource='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', source='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', _file='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$defaults.css', symbol='CloseButtonOver', _line='1410')]
    private static var _embed_css_Assets_swf_CloseButtonOver_804238466:Class;

    public static function init(fbs:IFlexModuleFactory):void
    {
        var style:CSSStyleDeclaration = StyleManager.getStyleDeclaration("TitleWindow");
    
        if (!style)
        {
            style = new CSSStyleDeclaration();
            StyleManager.setStyleDeclaration("TitleWindow", style, false);
        }
    
        if (style.defaultFactory == null)
        {
            style.defaultFactory = function():void
            {
                this.paddingTop = 4;
                this.paddingLeft = 4;
                this.cornerRadius = 8;
                this.paddingRight = 4;
                this.dropShadowEnabled = true;
                this.closeButtonDownSkin = _embed_css_Assets_swf_CloseButtonDown_1436987280;
                this.closeButtonOverSkin = _embed_css_Assets_swf_CloseButtonOver_804238466;
                this.closeButtonUpSkin = _embed_css_Assets_swf_CloseButtonUp_270378229;
                this.closeButtonDisabledSkin = _embed_css_Assets_swf_CloseButtonDisabled_1090433878;
                this.paddingBottom = 4;
                this.backgroundColor = 0xffffff;
            };
        }
    }
}

}
