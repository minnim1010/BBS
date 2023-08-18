import React from "react";
import { Button, Form, Input, Select } from "antd";
import TextArea from "antd/es/input/TextArea";

function PostWithAction(props) {
  const { model, state, action } = props;

  const changeCategory = (value) => {
    model.category = value;
  };

  const changeTitle = (event) => {
    model.title = event.target.value;
  };

  const changeContent = (event) => {
    model.content = event.target.value;
  };

  return (
    <Form>
      <Form.Item>
        <Select
          defaultValue="string"
          style={{ width: 120 }}
          onChange={changeCategory}
          options={[
            { value: "string", label: "string" },
            { value: "Java", label: "Java" },
            { value: "Spring", label: "Spring" },
          ]}
        />
      </Form.Item>
      <Form.Item>
        <Input
          type="text"
          placeholder="제목"
          value={state.title}
          onChange={changeTitle}
        />
      </Form.Item>
      <Form.Item>
        <TextArea rows={20} onChange={changeContent} value={state.content} />
      </Form.Item>
      <Button onClick={action}>확인</Button>
    </Form>

    // <div>
    //   <select name="category" id="category-select" onChange={changeCategory}>
    //     <option value="string">string</option>
    //     <option value="Java">Java</option>
    //     <option value="Spring">Spring</option>
    //   </select>
    // </div>
    // <div>
    //   <input
    //     type="text"
    //     placeholder="제목"
    //     value={state.title}
    //     onChange={changeTitle}
    //   ></input>
    // </div>
    // <div>
    //   <textarea
    //     type="text"
    //     placeholder="내용"
    //     value={state.content}
    //     onChange={changeContent}
    //   ></textarea>
    // </div>
    // <button onClick={action}>확인</button>
  );
}

export default PostWithAction;
