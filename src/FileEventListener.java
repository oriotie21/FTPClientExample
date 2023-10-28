//파일 전송 중 및 완료 시 콜백되는 함수 목록
interface FileEventListener{
    void onProgressChanged(int currentByte);
    void onProgressFinished();
}