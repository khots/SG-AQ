package 
{

import flash.display.Sprite;
import mx.core.IFlexModuleFactory;
import mx.core.mx_internal;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

[ExcludeClass]

public class _DateFieldStyle
{
    [Embed(_pathsep='true', _resolvedSource='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', source='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$Assets.swf', _file='C:/Program Files/Adobe/Flex Builder 3/sdks/2.0.1/frameworks/libs/framework.swc$defaults.css', symbol='openDateOver', _line='594')]
    private static var _embed_css_Assets_swf_openDateOver_756819568:Class;

    public static function init(fbs:IFlexModuleFactory):void
    {
        var style:CSSStyleDeclaration = StyleManager.getStyleDeclaration("DateField");
    
        if (!style)
        {
            style = new CSSStyleDeclaration();
            StyleManager.setStyleDeclaration("DateField", style, false);
        }
    
        if (style.defaultFactory == null)
        {
            style.defaultFactory = function():void
            {
                this.upSkin = _embed_css_Assets_swf_openDateOver_756819568;
                this.overSkin = _embed_css_Assets_swf_openDateOver_756819568;
                this.downSkin = _embed_css_Assets_swf_openDateOver_756819568;
                this.dateChooserStyleName = "dateFieldPopup";
                this.disabledSkin = _embed_css_Assets_swf_openDateOver_756819568;
            };
        }
    }
}

}
