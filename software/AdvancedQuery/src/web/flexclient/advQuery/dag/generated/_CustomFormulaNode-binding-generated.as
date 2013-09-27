
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
import Components.CustomFormulaNode;
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
	 * generated bindable wrapper for property customFormula (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'customFormula' moved to '_339987637customFormula'
	 */

    [Bindable(event="propertyChange")]
    public function get customFormula():String
    {
        return this._339987637customFormula;
    }

    public function set customFormula(value:String):void
    {
    	var oldValue:Object = this._339987637customFormula;
        if (oldValue !== value)
        {
			this._339987637customFormula = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "customFormula", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property currentCustomNode (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'currentCustomNode' moved to '_637246612currentCustomNode'
	 */

    [Bindable(event="propertyChange")]
    public function get currentCustomNode():CustomFormulaNode
    {
        return this._637246612currentCustomNode;
    }

    public function set currentCustomNode(value:CustomFormulaNode):void
    {
    	var oldValue:Object = this._637246612currentCustomNode;
        if (oldValue !== value)
        {
			this._637246612currentCustomNode = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "currentCustomNode", oldValue, value));
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


}
