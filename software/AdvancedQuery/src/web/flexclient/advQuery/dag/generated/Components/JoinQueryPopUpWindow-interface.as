package Components
{
import flash.accessibility.*;
import flash.debugger.*;
import flash.display.*;
import flash.errors.*;
import flash.events.*;
import flash.external.*;
import flash.filters.*;
import flash.geom.*;
import flash.media.*;
import flash.net.*;
import flash.printing.*;
import flash.profiler.*;
import flash.system.*;
import flash.text.*;
import flash.ui.*;
import flash.utils.*;
import flash.xml.*;
import mx.binding.*;
import mx.containers.TitleWindow;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.CheckBox;
import mx.controls.Label;
import mx.core.ClassFactory;
import mx.core.DeferredInstanceFromClass;
import mx.core.DeferredInstanceFromFunction;
import mx.core.IDeferredInstance;
import mx.core.IFactory;
import mx.core.IPropertyChangeNotifier;
import mx.core.mx_internal;
import mx.styles.*;
import mx.containers.HBox;
import mx.controls.Button;
import mx.containers.VBox;
import mx.containers.TitleWindow;

public class JoinQueryPopUpWindow extends mx.containers.TitleWindow
{
	public function JoinQueryPopUpWindow() {}

	[Bindable]
	public var ClassName1 : mx.controls.Label;
	[Bindable]
	public var entityLabel1 : mx.controls.Label;
	[Bindable]
	public var ClassName2 : mx.controls.Label;
	[Bindable]
	public var entityLabel2 : mx.controls.Label;
	[Bindable]
	public var deleteAllChkBox : mx.controls.CheckBox;
	[Bindable]
	public var deleteAllLabel : mx.controls.Label;
	[Bindable]
	public var deleteButton : mx.controls.Button;
	[Bindable]
	public var addAttributeId : mx.containers.VBox;
	[Bindable]
	public var submitButton : mx.controls.Button;

	public var _bindingsByDestination : Object;
	public var _bindingsBeginWithWord : Object;

include "E:/workplace/AdvancedQueryForJoin_NewCP/flexclient/advQuery/dag/Components/JoinQueryPopUpWindow.mxml:8,420";

}}
