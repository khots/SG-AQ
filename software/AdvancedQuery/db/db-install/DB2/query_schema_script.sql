
drop table COMMONS_GRAPH;
drop table COMMONS_GRAPH_EDGE;
drop table COMMONS_GRAPH_TO_EDGES;
drop table COMMONS_GRAPH_TO_VERTICES;
drop table QUERY;
drop table QUERY_ARITHMETIC_OPERAND;
drop table QUERY_BASEEXPR_TO_CONNECTORS;
drop table QUERY_BASE_EXPRESSION;
drop table QUERY_BASE_EXPR_OPND;
drop table QUERY_CONDITION;
drop table QUERY_CONDITION_VALUES;
drop table QUERY_CONNECTOR;
drop table QUERY_CONSTRAINTS;
drop table QUERY_CONSTRAINT_TO_EXPR;
drop table QUERY_CUSTOM_FORMULA;
drop table QUERY_EXPRESSION;
drop table QUERY_FORMULA_RHS;
drop table QUERY_INTER_MODEL_ASSOCIATION;
drop table QUERY_INTRA_MODEL_ASSOCIATION;
drop table QUERY_JOIN_GRAPH;
drop table QUERY_MODEL_ASSOCIATION;
drop table QUERY_OPERAND;
drop table QUERY_OUTPUT_ATTRIBUTE;
drop table QUERY_OUTPUT_TERM;
drop table QUERY_PARAMETER;
drop table QUERY_PARAMETERIZED_QUERY;
drop table QUERY_QUERY_ENTITY;
drop table QUERY_RULE_COND;
drop table QUERY_SUBEXPR_OPERAND;
drop table QUERY_TO_OUTPUT_TERMS;
drop table QUERY_TO_PARAMETERS;


create table COMMONS_GRAPH (IDENTIFIER bigint not null , primary key (IDENTIFIER));
create table COMMONS_GRAPH_EDGE (IDENTIFIER bigint not null , SOURCE_VERTEX_CLASS varchar(255), SOURCE_VERTEX_ID bigint, TARGET_VERTEX_CLASS varchar(255), TARGET_VERTEX_ID bigint, EDGE_CLASS varchar(255), EDGE_ID bigint, primary key (IDENTIFIER));
create table COMMONS_GRAPH_TO_EDGES (GRAPH_ID bigint not null, EDGE_ID bigint not null unique, primary key (GRAPH_ID, EDGE_ID));
create table COMMONS_GRAPH_TO_VERTICES (GRAPH_ID bigint not null, VERTEX_CLASS varchar(255), VERTEX_ID bigint);
/* for DB2 CONSTRAINTS_ID is set to not null*/
create table QUERY (IDENTIFIER bigint not null , CONSTRAINTS_ID bigint not null unique, primary key (IDENTIFIER));
create table QUERY_ARITHMETIC_OPERAND (IDENTIFIER bigint not null, LITERAL varchar(255), TERM_TYPE varchar(255), DATE_LITERAL date, TIME_INTERVAL varchar(255), DE_ATTRIBUTE_ID bigint, EXPRESSION_ID bigint, primary key (IDENTIFIER));
create table QUERY_BASEEXPR_TO_CONNECTORS (BASE_EXPRESSION_ID bigint not null, CONNECTOR_ID bigint not null, POSITION integer not null, primary key (BASE_EXPRESSION_ID, POSITION));
create table QUERY_BASE_EXPRESSION (IDENTIFIER bigint not null , EXPR_TYPE varchar(255) not null, primary key (IDENTIFIER));
create table QUERY_BASE_EXPR_OPND (BASE_EXPRESSION_ID bigint not null, OPERAND_ID bigint not null, POSITION integer not null, primary key (BASE_EXPRESSION_ID, POSITION));
create table QUERY_CONDITION (IDENTIFIER bigint not null , ATTRIBUTE_ID bigint not null, RELATIONAL_OPERATOR varchar(255), primary key (IDENTIFIER));
create table QUERY_CONDITION_VALUES (CONDITION_ID bigint not null, VALUE varchar(255), POSITION integer not null, primary key (CONDITION_ID, POSITION));
create table QUERY_CONNECTOR (IDENTIFIER bigint not null , OPERATOR varchar(255), NESTING_NUMBER integer, primary key (IDENTIFIER));
/* for DB2 QUERY_JOIN_GRAPH_ID is set to not null */
create table QUERY_CONSTRAINTS (IDENTIFIER bigint not null , QUERY_JOIN_GRAPH_ID bigint not null unique, primary key (IDENTIFIER));
create table QUERY_CONSTRAINT_TO_EXPR (CONSTRAINT_ID bigint not null, EXPRESSION_ID bigint not null unique, primary key (CONSTRAINT_ID, EXPRESSION_ID));
create table QUERY_CUSTOM_FORMULA (IDENTIFIER bigint not null, OPERATOR varchar(255), LHS_TERM_ID bigint, primary key (IDENTIFIER));
create table QUERY_EXPRESSION (IDENTIFIER bigint not null, IS_IN_VIEW DECIMAL(1,0), IS_VISIBLE DECIMAL(1,0), UI_EXPR_ID integer, QUERY_ENTITY_ID bigint, primary key (IDENTIFIER));
create table QUERY_FORMULA_RHS (CUSTOM_FORMULA_ID bigint not null, RHS_TERM_ID bigint not null, POSITION integer not null, primary key (CUSTOM_FORMULA_ID, POSITION));
create table QUERY_INTER_MODEL_ASSOCIATION (IDENTIFIER bigint not null, SOURCE_SERVICE_URL varchar(1000) not null, TARGET_SERVICE_URL varchar(1000) not null, SOURCE_ATTRIBUTE_ID bigint not null, TARGET_ATTRIBUTE_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_INTRA_MODEL_ASSOCIATION (IDENTIFIER bigint not null, DE_ASSOCIATION_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_JOIN_GRAPH (IDENTIFIER bigint not null , COMMONS_GRAPH_ID bigint, primary key (IDENTIFIER));
create table QUERY_MODEL_ASSOCIATION (IDENTIFIER bigint not null , primary key (IDENTIFIER));
create table QUERY_OPERAND (IDENTIFIER bigint not null , OPND_TYPE varchar(255) not null, primary key (IDENTIFIER));
create table QUERY_OUTPUT_ATTRIBUTE (IDENTIFIER bigint not null , EXPRESSION_ID bigint, ATTRIBUTE_ID bigint not null, PARAMETERIZED_QUERY_ID bigint, POSITION integer, primary key (IDENTIFIER));
create table QUERY_OUTPUT_TERM (IDENTIFIER bigint not null , NAME varchar(255), TIME_INTERVAL varchar(255), TERM_ID bigint, primary key (IDENTIFIER));
create table QUERY_PARAMETER (IDENTIFIER bigint not null , NAME varchar(255), OBJECT_CLASS varchar(255), OBJECT_ID bigint, primary key (IDENTIFIER));
/* for DB2 QUERY_NAME is set to not null  */
create table QUERY_PARAMETERIZED_QUERY (IDENTIFIER bigint not null, QUERY_NAME varchar(255) not null unique, DESCRIPTION varchar(1024), primary key (IDENTIFIER));
create table QUERY_QUERY_ENTITY (IDENTIFIER bigint not null , ENTITY_ID bigint not null, primary key (IDENTIFIER));
create table QUERY_RULE_COND (RULE_ID bigint not null, CONDITION_ID bigint not null, POSITION integer not null, primary key (RULE_ID, POSITION));
create table QUERY_SUBEXPR_OPERAND (IDENTIFIER bigint not null, EXPRESSION_ID bigint, primary key (IDENTIFIER));
create table QUERY_TO_OUTPUT_TERMS (QUERY_ID bigint not null, OUTPUT_TERM_ID bigint not null unique, POSITION integer not null, primary key (QUERY_ID, POSITION));
create table QUERY_TO_PARAMETERS (QUERY_ID bigint not null, PARAMETER_ID bigint not null unique, POSITION integer not null, primary key (QUERY_ID, POSITION));



alter table COMMONS_GRAPH_TO_EDGES add constraint FKA6B0D8BAA0494B1D foreign key (GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table COMMONS_GRAPH_TO_EDGES add constraint FKA6B0D8BAFAEF80D foreign key (EDGE_ID) references COMMONS_GRAPH_EDGE (IDENTIFIER);
alter table COMMONS_GRAPH_TO_VERTICES add constraint FK2C4412F5A0494B1D foreign key (GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table QUERY add constraint FK49D20A89E2FD9C7 foreign key (CONSTRAINTS_ID) references QUERY_CONSTRAINTS (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0BD635BD31 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0B96C7CE5A foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0BD006BE44 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0B7223B197 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0B687BE69E foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_ARITHMETIC_OPERAND add constraint FK262AEB0BE92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_BASEEXPR_TO_CONNECTORS add constraint FK3F0043482FCE1DA7 foreign key (CONNECTOR_ID) references QUERY_CONNECTOR (IDENTIFIER);
alter table QUERY_BASEEXPR_TO_CONNECTORS add constraint FK3F00434848BA6890 foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_BASE_EXPR_OPND add constraint FKAE67EAF0712A4C foreign key (OPERAND_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_BASE_EXPR_OPND add constraint FKAE67EA48BA6890 foreign key (BASE_EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_CONDITION_VALUES add constraint FK9997379D6458C2E7 foreign key (CONDITION_ID) references QUERY_CONDITION (IDENTIFIER);
alter table QUERY_CONSTRAINTS add constraint FKE364FCFF1C7EBF3B foreign key (QUERY_JOIN_GRAPH_ID) references QUERY_JOIN_GRAPH (IDENTIFIER);
alter table QUERY_CONSTRAINT_TO_EXPR add constraint FK2BD705CEA0A5F4C0 foreign key (CONSTRAINT_ID) references QUERY_CONSTRAINTS (IDENTIFIER);
alter table QUERY_CONSTRAINT_TO_EXPR add constraint FK2BD705CEE92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_CUSTOM_FORMULA add constraint FK5C0EEAEFBE674D45 foreign key (LHS_TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_CUSTOM_FORMULA add constraint FK5C0EEAEF12D455EB foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_EXPRESSION add constraint FK1B473A8F40EB75D4 foreign key (IDENTIFIER) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_EXPRESSION add constraint FK1B473A8F635766D8 foreign key (QUERY_ENTITY_ID) references QUERY_QUERY_ENTITY (IDENTIFIER);
alter table QUERY_FORMULA_RHS add constraint FKAE90F94D3BC37DCB foreign key (RHS_TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_FORMULA_RHS add constraint FKAE90F94D9A0B7164 foreign key (CUSTOM_FORMULA_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_INTER_MODEL_ASSOCIATION add constraint FKD70658D15F5AB67E foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION (IDENTIFIER);
alter table QUERY_INTRA_MODEL_ASSOCIATION add constraint FKF1EDBDD35F5AB67E foreign key (IDENTIFIER) references QUERY_MODEL_ASSOCIATION (IDENTIFIER);
alter table QUERY_JOIN_GRAPH add constraint FK2B41B5D09DBC4D94 foreign key (COMMONS_GRAPH_ID) references COMMONS_GRAPH (IDENTIFIER);
alter table QUERY_OUTPUT_ATTRIBUTE add constraint FK22C9DB75604D4BDA foreign key (PARAMETERIZED_QUERY_ID) references QUERY_PARAMETERIZED_QUERY (IDENTIFIER);
alter table QUERY_OUTPUT_ATTRIBUTE add constraint FK22C9DB75E92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_OUTPUT_TERM add constraint FK13C8A3D388C86B0D foreign key (TERM_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_PARAMETERIZED_QUERY add constraint FKA272176B76177EFE foreign key (IDENTIFIER) references QUERY (IDENTIFIER);
alter table QUERY_RULE_COND add constraint FKC32D37AE6458C2E7 foreign key (CONDITION_ID) references QUERY_CONDITION (IDENTIFIER);
alter table QUERY_RULE_COND add constraint FKC32D37AE39F0A10D foreign key (RULE_ID) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_SUBEXPR_OPERAND add constraint FK2BF760E832E875C8 foreign key (IDENTIFIER) references QUERY_OPERAND (IDENTIFIER);
alter table QUERY_SUBEXPR_OPERAND add constraint FK2BF760E8E92C814D foreign key (EXPRESSION_ID) references QUERY_BASE_EXPRESSION (IDENTIFIER);
alter table QUERY_TO_OUTPUT_TERMS add constraint FK8A70E2565E5B9430 foreign key (OUTPUT_TERM_ID) references QUERY_OUTPUT_TERM (IDENTIFIER);
alter table QUERY_TO_OUTPUT_TERMS add constraint FK8A70E25691051647 foreign key (QUERY_ID) references QUERY (IDENTIFIER);
alter table QUERY_TO_PARAMETERS add constraint FK8060DAD7F84B9027 foreign key (PARAMETER_ID) references QUERY_PARAMETER (IDENTIFIER);
alter table QUERY_TO_PARAMETERS add constraint FK8060DAD739F0A314 foreign key (QUERY_ID) references QUERY_PARAMETERIZED_QUERY (IDENTIFIER);


