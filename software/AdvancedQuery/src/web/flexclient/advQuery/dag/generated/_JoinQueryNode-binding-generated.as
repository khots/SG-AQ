
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.utils.ObjectProxy;
import mx.utils.UIDUtil;

import String;
import mx.containers.HBox;
import Array;
import Components.JoinQueryNode;
import Object;
import mx.controls.Label;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property dNodeFormula (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'dNodeFormula' moved to '_1817515520dNodeFormula'
	 */

    [Bindable(event="propertyChange")]
    public function get dNodeFormula():mx.containers.HBox
    {
        return this._1817515520dNodeFormula;
    }

    public function set dNodeFormula(value:mx.containers.HBox):void
    {
    	var oldValue:Object = this._1817515520dNodeFormula;
        if (oldValue !== value)
        {
			this._1817515520dNodeFormula = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "dNodeFormula", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property formulaLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'formulaLabel' moved to '_21615314formulaLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get formulaLabel():mx.controls.Label
    {
        return this._21615314formulaLabel;
    }

    public function set formulaLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._21615314formulaLabel;
        if (oldValue !== value)
        {
			this._21615314formulaLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "formulaLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myMenuData (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myMenuData' moved to '_143411339myMenuData'
	 */

    [Bindable(event="propertyChange")]
    public function get myMenuData():Array
    {
        return this._143411339myMenuData;
    }

    public function set myMenuData(value:Array):void
    {
    	var oldValue:Object = this._143411339myMenuData;
        if (oldValue !== value)
        {
			this._143411339myMenuData = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myMenuData", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property outputMenuData (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'outputMenuData' moved to '_1174576586outputMenuData'
	 */

    [Bindable(event="propertyChange")]
    public function get outputMenuData():Array
    {
        return this._1174576586outputMenuData;
    }

    public function set outputMenuData(value:Array):void
    {
    	var oldValue:Object = this._1174576586outputMenuData;
        if (oldValue !== value)
        {
			this._1174576586outputMenuData = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "outputMenuData", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property joinFormula (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'joinFormula' moved to '_1077201116joinFormula'
	 */

    [Bindable(event="propertyChange")]
    public function get joinFormula():String
    {
        return this._1077201116joinFormula;
    }

    public function set joinFormula(value:String):void
    {
    	var oldValue:Object = this._1077201116joinFormula;
        if (oldValue !== value)
        {
			this._1077201116joinFormula = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "joinFormula", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property currentJoinQueryNode (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'currentJoinQueryNode' moved to '_1466099687currentJoinQueryNode'
	 */

    [Bindable(event="propertyChange")]
    public function get currentJoinQueryNode():JoinQueryNode
    {
        return this._1466099687currentJoinQueryNode;
    }

    public function set currentJoinQueryNode(value:JoinQueryNode):void
    {
    	var oldValue:Object = this._1466099687currentJoinQueryNode;
        if (oldValue !== value)
        {
			this._1466099687currentJoinQueryNode = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "currentJoinQueryNode", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property obj (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'obj' moved to '_109815obj'
	 */

    [Bindable(event="propertyChange")]
    public function get obj():Object
    {
        return this._109815obj;
    }

    public function set obj(value:Object):void
    {
    	var oldValue:Object = this._109815obj;
        if (oldValue !== value)
        {
			this._109815obj = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "obj", oldValue, value));
        }
    }


}
