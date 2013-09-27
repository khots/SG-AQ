
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.utils.ObjectProxy;
import mx.utils.UIDUtil;

import String;
import mx.controls.Button;
import mx.containers.VBox;
import mx.controls.Label;
import mx.containers.Canvas;
import mx.rpc.remoting.mxml.RemoteObject;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property JoinQueryBtn (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'JoinQueryBtn' moved to '_1851433950JoinQueryBtn'
	 */

    [Bindable(event="propertyChange")]
    public function get JoinQueryBtn():mx.controls.Button
    {
        return this._1851433950JoinQueryBtn;
    }

    public function set JoinQueryBtn(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._1851433950JoinQueryBtn;
        if (oldValue !== value)
        {
			this._1851433950JoinQueryBtn = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "JoinQueryBtn", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property TQBtn (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'TQBtn' moved to '_80055967TQBtn'
	 */

    [Bindable(event="propertyChange")]
    public function get TQBtn():mx.controls.Button
    {
        return this._80055967TQBtn;
    }

    public function set TQBtn(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._80055967TQBtn;
        if (oldValue !== value)
        {
			this._80055967TQBtn = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "TQBtn", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property currentExp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'currentExp' moved to '_1088997916currentExp'
	 */

    [Bindable(event="propertyChange")]
    public function get currentExp():mx.controls.Label
    {
        return this._1088997916currentExp;
    }

    public function set currentExp(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._1088997916currentExp;
        if (oldValue !== value)
        {
			this._1088997916currentExp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "currentExp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property mainPanel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'mainPanel' moved to '_265437237mainPanel'
	 */

    [Bindable(event="propertyChange")]
    public function get mainPanel():mx.containers.Canvas
    {
        return this._265437237mainPanel;
    }

    public function set mainPanel(value:mx.containers.Canvas):void
    {
    	var oldValue:Object = this._265437237mainPanel;
        if (oldValue !== value)
        {
			this._265437237mainPanel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "mainPanel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property mainPanelx (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'mainPanelx' moved to '_361380365mainPanelx'
	 */

    [Bindable(event="propertyChange")]
    public function get mainPanelx():mx.containers.VBox
    {
        return this._361380365mainPanelx;
    }

    public function set mainPanelx(value:mx.containers.VBox):void
    {
    	var oldValue:Object = this._361380365mainPanelx;
        if (oldValue !== value)
        {
			this._361380365mainPanelx = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "mainPanelx", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property rpcService (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'rpcService' moved to '_1405769456rpcService'
	 */

    [Bindable(event="propertyChange")]
    public function get rpcService():mx.rpc.remoting.mxml.RemoteObject
    {
        return this._1405769456rpcService;
    }

    public function set rpcService(value:mx.rpc.remoting.mxml.RemoteObject):void
    {
    	var oldValue:Object = this._1405769456rpcService;
        if (oldValue !== value)
        {
			this._1405769456rpcService = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "rpcService", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property operationforView (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'operationforView' moved to '_920041529operationforView'
	 */

    [Bindable(event="propertyChange")]
    public function get operationforView():String
    {
        return this._920041529operationforView;
    }

    public function set operationforView(value:String):void
    {
    	var oldValue:Object = this._920041529operationforView;
        if (oldValue !== value)
        {
			this._920041529operationforView = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "operationforView", oldValue, value));
        }
    }


}
