






package
{
import flash.display.Sprite;
import mx.core.IFlexModuleFactory;
import mx.binding.ArrayElementWatcher;
import mx.binding.FunctionReturnWatcher;
import mx.binding.IWatcherSetupUtil;
import mx.binding.PropertyWatcher;
import mx.binding.RepeaterComponentWatcher;
import mx.binding.RepeaterItemWatcher;
import mx.binding.XMLWatcher;
import mx.binding.Watcher;

[ExcludeClass]
[Mixin]
public class _Components_JoinQueryPopUpWindowWatcherSetupUtil extends Sprite
    implements mx.binding.IWatcherSetupUtil
{
    public function _Components_JoinQueryPopUpWindowWatcherSetupUtil()
    {
        super();
    }

    public static function init(fbs:IFlexModuleFactory):void
    {
        import Components.JoinQueryPopUpWindow;
        (Components.JoinQueryPopUpWindow).watcherSetupUtil = new _Components_JoinQueryPopUpWindowWatcherSetupUtil();
    }

    public function setup(target:Object,
                          propertyGetter:Function,
                          bindings:Array,
                          watchers:Array):void
    {
        import flash.events.EventDispatcher;
        import mx.core.DeferredInstanceFromFunction;
        import mx.events.CloseEvent;
        import mx.containers.HBox;
        import mx.managers.PopUpManager;
        import mx.containers.TitleWindow;
        import mx.core.IDeferredInstance;
        import mx.core.ClassFactory;
        import mx.core.mx_internal;
        import mx.core.IPropertyChangeNotifier;
        import mx.utils.ObjectProxy;
        import mx.controls.Label;
        import mx.controls.Button;
        import mx.utils.UIDUtil;
        import mx.controls.Alert;
        import flash.events.MouseEvent;
        import mx.controls.CheckBox;
        import mx.rpc.events.ResultEvent;
        import mx.controls.ComboBox;
        import mx.core.UIComponentDescriptor;
        import mx.containers.VBox;
        import mx.collections.ArrayCollection;
        import mx.events.PropertyChangeEvent;
        import flash.events.Event;
        import mx.core.IFactory;
        import mx.core.DeferredInstanceFromClass;
        import mx.binding.BindingManager;
        import flash.events.IEventDispatcher;

        var tempWatcher:mx.binding.Watcher;

        // writeWatcher id=1 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher shouldWriteChildren=true
        watchers[1] = new mx.binding.PropertyWatcher("firstEntityLabel",
            {
                propertyChange: true
            }
                                                                 );

        // writeWatcher id=3 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher shouldWriteChildren=true
        watchers[3] = new mx.binding.PropertyWatcher("buttonLabel",
            {
                propertyChange: true
            }
                                                                 );

        // writeWatcher id=2 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher shouldWriteChildren=true
        watchers[2] = new mx.binding.PropertyWatcher("secondEntityLabel",
            {
                propertyChange: true
            }
                                                                 );


        // writeWatcherBottom id=1 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher
        // writePropertyWatcherBottom id=1 size=2
        tempWatcher = watchers[1];
        tempWatcher.addListener(bindings[1]);
        tempWatcher.addListener(bindings[0]);
        watchers[1].propertyGetter = propertyGetter;
        watchers[1].updateParent(target);

 





        // writeWatcherBottom id=3 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher
        // writePropertyWatcherBottom id=3 size=2
        tempWatcher = watchers[3];
        tempWatcher.addListener(bindings[4]);
        tempWatcher.addListener(bindings[5]);
        watchers[3].propertyGetter = propertyGetter;
        watchers[3].updateParent(target);

 





        // writeWatcherBottom id=2 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher
        // writePropertyWatcherBottom id=2 size=2
        tempWatcher = watchers[2];
        tempWatcher.addListener(bindings[2]);
        tempWatcher.addListener(bindings[3]);
        watchers[2].propertyGetter = propertyGetter;
        watchers[2].updateParent(target);

 






        bindings[0].uiComponentWatcher = 1;
        bindings[0].execute();
        bindings[1].uiComponentWatcher = 1;
        bindings[1].execute();
        bindings[2].uiComponentWatcher = 2;
        bindings[2].execute();
        bindings[3].uiComponentWatcher = 2;
        bindings[3].execute();
        bindings[4].uiComponentWatcher = 3;
        bindings[4].execute();
        bindings[5].uiComponentWatcher = 3;
        bindings[5].execute();
    }
}

}
