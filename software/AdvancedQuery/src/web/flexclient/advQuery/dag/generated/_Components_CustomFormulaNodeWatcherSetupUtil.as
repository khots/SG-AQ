






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
public class _Components_CustomFormulaNodeWatcherSetupUtil extends Sprite
    implements mx.binding.IWatcherSetupUtil
{
    public function _Components_CustomFormulaNodeWatcherSetupUtil()
    {
        super();
    }

    public static function init(fbs:IFlexModuleFactory):void
    {
        import Components.CustomFormulaNode;
        (Components.CustomFormulaNode).watcherSetupUtil = new _Components_CustomFormulaNodeWatcherSetupUtil();
    }

    public function setup(target:Object,
                          propertyGetter:Function,
                          bindings:Array,
                          watchers:Array):void
    {
        import flash.events.EventDispatcher;
        import mx.core.DeferredInstanceFromFunction;
        import mx.containers.HBox;
        import mx.core.IDeferredInstance;
        import mx.core.ClassFactory;
        import mx.core.mx_internal;
        import mx.core.IPropertyChangeNotifier;
        import mx.controls.Menu;
        import Components.DAGConstants;
        import mx.utils.ObjectProxy;
        import mx.controls.Label;
        import flash.utils.IExternalizable;
        import mx.controls.Button;
        import mx.utils.UIDUtil;
        import mx.controls.Alert;
        import flash.events.MouseEvent;
        import mx.events.MenuEvent;
        import mx.containers.Box;
        import mx.rpc.events.ResultEvent;
        import mx.core.UIComponentDescriptor;
        import mx.containers.VBox;
        import mx.events.PropertyChangeEvent;
        import flash.events.Event;
        import mx.core.IFactory;
        import mx.core.DeferredInstanceFromClass;
        import mx.rpc.events.FaultEvent;
        import mx.binding.BindingManager;
        import flash.events.IEventDispatcher;

        var tempWatcher:mx.binding.Watcher;

        // writeWatcher id=1 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher shouldWriteChildren=true
        watchers[1] = new mx.binding.PropertyWatcher("customFormula",
            {
                propertyChange: true
            }
                                                                 );


        // writeWatcherBottom id=1 shouldWriteSelf=true class=flex2.compiler.as3.binding.PropertyWatcher
        // writePropertyWatcherBottom id=1 size=1
        watchers[1].addListener(bindings[0]);
        watchers[1].propertyGetter = propertyGetter;
        watchers[1].updateParent(target);

 






        bindings[0].uiComponentWatcher = 1;
        bindings[0].execute();
    }
}

}
