package com.training.spring.template;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    @Deprecated
    public int deprecatedCalcSum(String filePath) throws IOException {
        BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
            @Override
            public int doSomethingWithReader(BufferedReader br) throws IOException {
                int sum = 0;
                String line = null;
                while ((line = br.readLine()) != null) {     // 한 줄씩 확인
                    sum += Integer.parseInt(line);
                }
                return sum;
            }
        };
        return fileReadTemplate(filePath, sumCallback);
    }

    public int calcSum(String filePath) throws IOException {
        LineCallback sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.parseInt(line);
            }
        };
        return lineReadTemplate(filePath, sumCallback, 0);
    }

    @Deprecated
    public int deprecatedCalcMultiply(String filePath) throws IOException {
        BufferedReaderCallback mulCallback = new BufferedReaderCallback() {
            @Override
            public int doSomethingWithReader(BufferedReader br) throws IOException {
                int mul = 1;
                String line = null;
                while ((line = br.readLine()) != null) {     // 한 줄씩 확인
                    mul *= Integer.parseInt(line);
                }
                return mul;
            }
        };
        return fileReadTemplate(filePath, mulCallback);
    }

    public Integer calcMultiply(String filePath) throws IOException {
        LineCallback<Integer> mulCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.parseInt(line);
            }
        };
        return lineReadTemplate(filePath, mulCallback, 1);
    }

    /**
     * 파일읽기 부분 템플릿(일관성)
     * @param callback 사용할 콜백 오브젝트(가변성)
     * @return 결과값
     */
    public int fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            int result = callback.doSomethingWithReader(br);        // callback 오브젝트 호출. 템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백 결과 받음

            return result;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            T result = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {     // 한 줄씩 확인
                result = callback.doSomethingWithLine(line, result);    // result에 이전 계산값을 저장해뒀다가 다음 줄 계산에 사용한다
            }
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
