import React from "react";
import { Button, Form } from "antd";
import TextArea from "antd/es/input/TextArea";

function CommentWithAction(props) {
  const { model, state, action } = props;

  const changeContent = (event) => {
    model.content = event.target.value;
  };

  return (
    <div className="comment-with-action">
      <Form>
        <Form.Item>
          <TextArea rows={5} onChange={changeContent} value={state.content} />
          <Button onClick={action} block>확인</Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default CommentWithAction;
