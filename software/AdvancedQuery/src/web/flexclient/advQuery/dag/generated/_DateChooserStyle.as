package 
{

import flash.display.Sprite;
import mx.core.IFlexModuleFactory;
import mx.core.mx_internal;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;
import mx.skins.halo.DateChooserIndicator;
import mx.skins.halo.DateChooserYearArrowSkin;
import mx.skins.halo.DateChooserMonthArrowSkin;

[ExcludeClass]

public class _DateChooserStyle
{

    public static function init(fbs:IFlexModuleFactory):void
    {
        var style:CSSStyleDeclaration = StyleManager.getStyleDeclaration("DateChooser");
    
        if (!style)
        {
            style = new CSSStyleDeclaration();
            StyleManager.setStyleDeclaration("DateChooser", style, false);
        }
    
        if (style.defaultFactory == null)
        {
            style.defaultFactory = function():void
            {
                this.todayStyleName = "todayStyle";
                this.prevYearDownSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.nextMonthOverSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.nextYearDownSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.prevMonthDisabledSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.cornerRadius = 4;
                this.rollOverIndicatorSkin = mx.skins.halo.DateChooserIndicator;
                this.nextYearDisabledSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.nextMonthDownSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.prevYearDisabledSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.prevMonthDownSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.nextMonthDisabledSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.prevYearUpSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.weekDayStyleName = "weekDayStyle";
                this.nextYearUpSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.todayIndicatorSkin = mx.skins.halo.DateChooserIndicator;
                this.headerStyleName = "headerDateText";
                this.nextYearOverSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.prevMonthOverSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.headerColors = [0xe1e5eb, 0xf4f5f7];
                this.prevYearOverSkin = mx.skins.halo.DateChooserYearArrowSkin;
                this.selectionIndicatorSkin = mx.skins.halo.DateChooserIndicator;
                this.prevMonthUpSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.nextMonthUpSkin = mx.skins.halo.DateChooserMonthArrowSkin;
                this.backgroundColor = 0xffffff;
            };
        }
    }
}

}
